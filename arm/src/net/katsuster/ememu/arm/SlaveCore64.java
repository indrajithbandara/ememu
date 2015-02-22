package net.katsuster.ememu.arm;

/**
 * 64 ビットアドレスバスのスレーブコア。
 *
 * 自身のタイミングで動作します。
 * また、バスからの読み取り、書き込み要求に応答する形でも動作します。
 *
 * バスからのアクセス時に用いるアドレスは 64 ビット幅です。
 *
 * @author katsuhiro
 */
public abstract class SlaveCore64 extends Core
        implements RWCore64 {
    public static long ADDR_MASK_8 = ~0x0L;
    public static long ADDR_MASK_16 = ~0x1L;
    public static long ADDR_MASK_32 = ~0x3L;
    public static long ADDR_MASK_64 = ~0x7L;
    public static long DATA_MASK_8 = 0xffL;
    public static long DATA_MASK_16 = 0xffffL;
    public static long DATA_MASK_32 = 0xffffffffL;
    public static long DATA_MASK_64 = 0xffffffffffffffffL;

    private Bus64 masterBus;

    /**
     * このスレーブコアが接続されているバスを取得します。
     *
     * @return コアが接続されているバス
     */
    public Bus64 getMasterBus() {
        return masterBus;
    }

    /**
     * このスレーブコアを接続するバスを設定します。
     *
     * @param bus コアを接続するバス
     */
    public void setMasterBus(Bus64 bus) {
        masterBus = bus;
    }

    /**
     * 指定されたデータ幅に対応するアドレスマスクを返します。
     *
     * アドレスマスクの値の例:
     * 8bits = ~0L
     *   => マスクなし
     * 16bits = ~1L
     *   => 下位 1ビットを消去するマスク => アドレスは 2の倍数
     * 32bits = ~3L
     *   => 下位 2ビットを消去するマスク => アドレスは 4の倍数
     *
     * @param dataLen データ幅
     * @return アドレスマスク
     */
    public static long getAddressMask(int dataLen) {
        switch (dataLen) {
        case 8:
            return ADDR_MASK_8;
        case 16:
            return ADDR_MASK_16;
        case 32:
            return ADDR_MASK_32;
        case 64:
            return ADDR_MASK_64;
        default:
            throw new IllegalArgumentException("Data length" +
                    String.format("(0x%08x) is not supported.", dataLen));
        }
    }

    /**
     * 指定されたデータ幅に対応するマスクを返します。
     *
     * マスクの値の例:
     * 8bits = 0xffL
     * 16bits = 0xffffL
     * 32bits = 0xffffffffL
     *
     * @param dataLen データ幅
     * @return データマスク
     */
    public static long getDataMask(int dataLen) {
        switch (dataLen) {
        case 8:
            return DATA_MASK_8;
        case 16:
            return DATA_MASK_16;
        case 32:
            return DATA_MASK_32;
        case 64:
            return DATA_MASK_64;
        default:
            throw new IllegalArgumentException("Data length" +
                    String.format("(0x%08x) is not supported.", dataLen));
        }
    }

    /**
     * リトルエンディアンにて、
     * 指定されたアドレスにあるワードを取得します。
     *
     * バスにはデータ幅の倍数のアドレスでのみアクセスできるものとします。
     * 例えば、
     * バスのデータ幅が 32bits であればアドレス 0, 4, 8, 12, ... 4n のみ、
     * バスのデータ幅が 64bits であればアドレス 0, 8, 16, 24, ... 8n のみ、
     * です。
     *
     * バスのデータ幅より小さいデータを取得するとき、
     * バス幅のデータを取得した後に得られた値をシフトして、
     * 目的のアドレスにあるデータを取得する必要があります。
     *
     * 例えば、バスのデータ幅が 64bits、データ幅が 16bits のシステムにて、
     * アドレス 0x12 のデータを取得するとします。
     *
     * バスのデータ幅は 64bits 幅のためアドレス 0x12 はアクセスできません。
     * 従って最も近い 8の倍数であるアドレス 0x10 から 64bits を読み出します。
     *
     * このときバスから読み出したデータが 0x1234_5678_0246_8ace だとします。
     * バスから読み出したデータを 16bits ごとに分割し、
     * 符号ビットから近い順（上位ビットから）から並べると、
     * 0x1234:
     * 0x5678:
     * 0x0246:
     * 0x8ace:
     * となります。
     *
     * リトルエンディアンシステムの場合、データの上位から、
     * アドレス+6, アドレス+4, アドレス+2, アドレス, に対応しますので、
     * 0x1234: アドレス+6
     * 0x5678: アドレス+4
     * 0x0246: アドレス+2
     * 0x8ace: アドレス
     * と対応します。
     *
     * 従って、目的のアドレス 0x12 にあるデータは 0x0246 となり、
     * バスから読み出したデータをシフトする量は 16bits です。
     *
     * 同様に 0x14 ならばデータは 0x5678 となり、シフトする量は 32bits です。
     *
     * @param addr     データのアドレス
     * @param data     バスから読んだデータ
     * @param busLen   バスのデータ幅（バイト単位）
     * @param dataLen  データ幅（バイト単位）
     * @return addr にあるデータ
     */
    public static long readMasked(long addr, long data, int busLen, int dataLen) {
        long busMask = getAddressMask(busLen);
        long dataMask = getAddressMask(dataLen);
        int sh = (int)(addr & ~busMask & dataMask) * 8;

        return data >> sh;
    }

    /**
     * リトルエンディアンにて、
     * 指定されたアドレスにあるワードを変更します。
     *
     * バスにはバスのデータ幅の倍数のアドレスでのみアクセスできるものとします。
     * 例えば、
     * バス幅が 32bits であればアドレス 0, 4, 8, 12, ... 4n のみ、
     * バス幅が 64bits であればアドレス 0, 8, 16, 24, ... 8n のみ、
     * です。
     *
     * バスのデータ幅より小さいデータ幅を変更するとき、
     * バスのデータ幅のデータを取得した後に得られた値をマスクして、
     * 目的のアドレスにあるデータを変更する必要があります。
     *
     * 例えば、データのバス幅が 64bits、データ幅が 16bits のシステムにて、
     * アドレス 0x12 のデータを変更するとします。
     *
     * バスのデータ幅は 64bits のためアドレス 0x12 はアクセスできません。
     * 従って最も近い 8の倍数であるアドレス 0x10 から 64bits を読み出します。
     *
     * このときバスから読み出したデータが 0x1234_5678_0246_8ace だとします。
     * バスから読み出したデータを 16bits ごとに分割し、
     * 符号ビットから近い順（上位ビットから）から並べると、
     * 0x1234:
     * 0x5678:
     * 0x0246:
     * 0x8ace:
     * となります。
     *
     * リトルエンディアンシステムの場合、データの上位から、
     * アドレス+6, アドレス+4, アドレス+2, アドレス, に対応しますので、
     * 0x1234: アドレス+6
     * 0x5678: アドレス+4
     * 0x0246: アドレス+2
     * 0x8ace: アドレス
     * と対応します。
     *
     * 従って、目的のアドレス 0x12 にあるデータは 0x0246 となり、
     * バスから読み出したデータを変更するためのシフト量は 16bits です。
     *
     * 同様に 0x14 ならばデータは 0x5678 となり、シフトする量は 32bits です。
     *
     * @param addr     データのアドレス
     * @param data     バスから読んだデータ
     * @param busLen   バスのデータ幅
     * @param dataLen  書き込むデータ幅
     * @param newData  addr に書き込むデータ
     * @return addr に newData を書き込んだ後のデータ
     */
    public static long writeMasked(long addr, long data, long newData, int busLen, int dataLen) {
        long busMask = getAddressMask(busLen);
        long dataMask = getAddressMask(dataLen);
        long eraseMask = getDataMask(dataLen);
        int sh = (int)(addr & ~busMask & dataMask) * 8;

        return (data & ~(eraseMask << sh)) | ((newData & eraseMask) << sh);
    }

    @Override
    public abstract boolean tryRead(long addr, int len);

    @Override
    public abstract byte read8(long addr);

    @Override
    public abstract short read16(long addr);

    @Override
    public abstract int read32(long addr);

    @Override
    public abstract long read64(long addr);

    @Override
    public abstract boolean tryWrite(long addr, int len);

    @Override
    public abstract void write8(long addr, byte data);

    @Override
    public abstract void write16(long addr, short data);

    @Override
    public abstract void write32(long addr, int data);

    @Override
    public abstract void write64(long addr, long data);
}
