package net.katsuster.ememu.arm;

/**
 * 64 ビットアドレスバスのマスターコア。
 *
 * 自身のタイミングで動作します。
 * バスに read/write 要求を出します。
 *
 * バスへのアクセス時に用いるアドレスは 64 ビット幅です。
 *
 * @author katsuhiro
 */
public abstract class MasterCore64 extends Core
        implements RWCore64 {
    private Bus64 slaveBus;

    /**
     * コアが接続されているスレーブバスを取得します。
     *
     * @return スレーブバス
     */
    public Bus64 getSlaveBus() {
        return slaveBus;
    }

    /**
     * スレーブバスにコアを接続します。
     *
     * @param bus スレーブバス
     */
    public void setSlaveBus(Bus64 bus) {
        slaveBus = bus;
    }

    @Override
    public boolean tryRead(long addr, int len) {
        return slaveBus.tryRead(addr, len);
    }

    @Override
    public byte read8(long addr) {
        return slaveBus.read8(addr);
    }

    @Override
    public short read16(long addr) {
        return slaveBus.read16(addr);
    }

    @Override
    public int read32(long addr) {
        return slaveBus.read32(addr);
    }

    @Override
    public long read64(long addr) {
        return slaveBus.read64(addr);
    }

    @Override
    public boolean tryWrite(long addr, int len) {
        return slaveBus.tryWrite(addr, len);
    }

    @Override
    public void write8(long addr, byte data) {
        slaveBus.write8(addr, data);
    }

    @Override
    public void write16(long addr, short data) {
        slaveBus.write16(addr, data);
    }

    @Override
    public void write32(long addr, int data) {
        slaveBus.write32(addr, data);
    }

    @Override
    public void write64(long addr, long data) {
        slaveBus.write64(addr, data);
    }
}
