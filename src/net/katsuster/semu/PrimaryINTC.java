package net.katsuster.semu;

import java.util.*;

/**
 * 割り込みコントローラ
 *
 * 参考: PrimeCell Vectored Interrupt Controller (PL190)
 * ARM DDI0181E
 *
 * @author katsuhiro
 */
public class PrimaryINTC extends Controller64Reg32 {
    private List<INTC> intcs;
    private int rawHardInt;
    private int rawSoftInt;
    private int intEnable;
    private int intSelect;

    public static final int MAX_INTCS = 32;

    public static final int REG_VICIRQSTATUS    = 0x000;
    public static final int REG_VICFIQSTATUS    = 0x004;
    public static final int REG_VICRAWINTR      = 0x008;
    public static final int REG_VICINTSELECT    = 0x00c;
    public static final int REG_VICINTENABLE    = 0x010;
    public static final int REG_VICINTENCLEAR   = 0x014;
    public static final int REG_VICSOFTINT      = 0x018;
    public static final int REG_VICSOFTINTCLEAR = 0x01c;
    public static final int REG_VICPROTECTION   = 0x020;
    public static final int REG_VICVECTADDR     = 0x030;
    public static final int REG_VICDEFVECTADDR  = 0x034;
    public static final int REG_VICVECTADDR0    = 0x100;
    public static final int REG_VICVECTADDR1    = 0x104;
    public static final int REG_VICVECTADDR2    = 0x108;
    public static final int REG_VICVECTADDR3    = 0x10c;
    public static final int REG_VICVECTADDR4    = 0x110;
    public static final int REG_VICVECTADDR5    = 0x114;
    public static final int REG_VICVECTADDR6    = 0x118;
    public static final int REG_VICVECTADDR7    = 0x11c;
    public static final int REG_VICVECTADDR8    = 0x120;
    public static final int REG_VICVECTADDR9    = 0x124;
    public static final int REG_VICVECTADDR10   = 0x128;
    public static final int REG_VICVECTADDR11   = 0x12c;
    public static final int REG_VICVECTADDR12   = 0x130;
    public static final int REG_VICVECTADDR13   = 0x134;
    public static final int REG_VICVECTADDR14   = 0x138;
    public static final int REG_VICVECTADDR15   = 0x13c;
    public static final int REG_VICVECTCNTL0    = 0x200;
    public static final int REG_VICVECTCNTL1    = 0x204;
    public static final int REG_VICVECTCNTL2    = 0x208;
    public static final int REG_VICVECTCNTL3    = 0x20c;
    public static final int REG_VICVECTCNTL4    = 0x210;
    public static final int REG_VICVECTCNTL5    = 0x214;
    public static final int REG_VICVECTCNTL6    = 0x218;
    public static final int REG_VICVECTCNTL7    = 0x21c;
    public static final int REG_VICVECTCNTL8    = 0x220;
    public static final int REG_VICVECTCNTL9    = 0x224;
    public static final int REG_VICVECTCNTL10   = 0x228;
    public static final int REG_VICVECTCNTL11   = 0x22c;
    public static final int REG_VICVECTCNTL12   = 0x230;
    public static final int REG_VICVECTCNTL13   = 0x234;
    public static final int REG_VICVECTCNTL14   = 0x238;
    public static final int REG_VICVECTCNTL15   = 0x23c;
    public static final int REG_VICITCR         = 0x300;
    public static final int REG_VICITIP1        = 0x304;
    public static final int REG_VICITIP2        = 0x308;
    public static final int REG_VICITOP1        = 0x30c;
    public static final int REG_VICITOP2        = 0x310;
    public static final int REG_VICPERIPHID0    = 0xfe0;
    public static final int REG_VICPERIPHID1    = 0xfe4;
    public static final int REG_VICPERIPHID2    = 0xfe8;
    public static final int REG_VICPERIPHID3    = 0xfec;
    public static final int REG_VICPCELLID0     = 0xff0;
    public static final int REG_VICPCELLID1     = 0xff4;
    public static final int REG_VICPCELLID2     = 0xff8;
    public static final int REG_VICPCELLID3     = 0xffc;

    public PrimaryINTC() {
        //割り込み元の初期化をします
        intcs = new ArrayList<INTC>();
        for (int i = 0; i < MAX_INTCS; i++) {
            connectINTC(i, new NullINTC());
        }

        //割り込みステータスの初期化を行います
        rawHardInt = 0;
        rawSoftInt = 0;
        intEnable = 0;
        intSelect = 0;

        //レジスタの定義を行う
        addReg(REG_VICIRQSTATUS, "VICIRQSTATUS", 0x00000000);
        addReg(REG_VICFIQSTATUS, "VICFIQSTATUS", 0x00000000);
        addReg(REG_VICRAWINTR, "VICRAWINTR", 0x00000000);
        addReg(REG_VICINTSELECT, "VICINTSELECT", 0x00000000);
        addReg(REG_VICINTENABLE, "VICINTENABLE", 0x00000000);
        addReg(REG_VICINTENCLEAR, "VICINTENCLEAR", 0x00000000);
        addReg(REG_VICSOFTINT, "VICSOFTINT", 0x00000000);
        addReg(REG_VICSOFTINTCLEAR, "VICSOFTINTCLEAR", 0x00000000);
        addReg(REG_VICVECTADDR, "VICVECTADDR", 0x00000000);
        addReg(REG_VICDEFVECTADDR, "VICDEFVECTADDR", 0x00000000);

        addReg(REG_VICVECTCNTL0, "VICVECTCNTL0", 0x00000000);
        addReg(REG_VICVECTCNTL1, "VICVECTCNTL1", 0x00000000);
        addReg(REG_VICVECTCNTL2, "VICVECTCNTL2", 0x00000000);
        addReg(REG_VICVECTCNTL3, "VICVECTCNTL3", 0x00000000);
        addReg(REG_VICVECTCNTL4, "VICVECTCNTL4", 0x00000000);
        addReg(REG_VICVECTCNTL5, "VICVECTCNTL5", 0x00000000);
        addReg(REG_VICVECTCNTL6, "VICVECTCNTL6", 0x00000000);
        addReg(REG_VICVECTCNTL7, "VICVECTCNTL7", 0x00000000);
        addReg(REG_VICVECTCNTL8, "VICVECTCNTL8", 0x00000000);
        addReg(REG_VICVECTCNTL9, "VICVECTCNTL9", 0x00000000);
        addReg(REG_VICVECTCNTL10, "VICVECTCNTL10", 0x00000000);
        addReg(REG_VICVECTCNTL11, "VICVECTCNTL11", 0x00000000);
        addReg(REG_VICVECTCNTL12, "VICVECTCNTL12", 0x00000000);
        addReg(REG_VICVECTCNTL13, "VICVECTCNTL13", 0x00000000);
        addReg(REG_VICVECTCNTL14, "VICVECTCNTL14", 0x00000000);
        addReg(REG_VICVECTCNTL15, "VICVECTCNTL15", 0x00000000);

        addReg(REG_VICITCR, "VICITCR", 0x00000000);

        addReg(REG_VICPERIPHID0, "VICPERIPHID0", 0x00000090);
        addReg(REG_VICPERIPHID1, "VICPERIPHID1", 0x00000011);
        //レジスタの説明（3.3.14）とリセット後の初期値（Table 3-1）が
        //矛盾しているため、レジスタの説明を正しいものとして実装する
        //[ 7: 4]: Revision   : must be 0x1
        //[ 3: 0]: Designer1  : must be 0x0
        addReg(REG_VICPERIPHID2, "VICPERIPHID2", 0x00000010);
        addReg(REG_VICPERIPHID3, "VICPERIPHID3", 0x00000000);
        addReg(REG_VICPCELLID0, "VICPCELLID0", 0x0000000d);
        addReg(REG_VICPCELLID1, "VICPCELLID1", 0x000000f0);
        addReg(REG_VICPCELLID2, "VICPCELLID2", 0x00000005);
        addReg(REG_VICPCELLID3, "VICPCELLID3", 0x000000b1);
    }

    /**
     * 割り込みコントローラにコアを接続します。
     *
     * 接続後、コアからの割り込みを受け付け、
     * 条件に応じて割り込みコントローラの接続先（大抵は CPU です）に、
     * 割り込みを要求します。
     *
     * @param n 0 から 31 までの割り込み線の番号
     * @param c コア
     */
    public void connectINTC(int n, INTC c) {
        if (n < 0 || MAX_INTCS <= n) {
            throw new IllegalArgumentException(String.format(
                    "Illegal IRQ source number %d.", n));
        }

        intcs.add(n, c);
    }

    /**
     * 割り込みコントローラからコアを切断します。
     *
     * 切断後はコアからの割り込みを受け付けません。
     *
     * @param n 0 から 31 までの割り込み線の番号
     */
    public void disconnectINTC(int n) {
        if (n < 0 || MAX_INTCS <= n) {
            throw new IllegalArgumentException(String.format(
                    "Illegal IRQ source number %d.", n));
        }

        intcs.add(n, new NullINTC());
    }

    /**
     * 指定された割り込み線に接続されているコアを返します。
     *
     * @param n 割り込み線の番号
     * @return コア
     */
    public INTC getINTC(int n) {
        if (n < 0 || MAX_INTCS <= n) {
            throw new IllegalArgumentException(String.format(
                    "Illegal IRQ source number %d.", n));
        }

        return intcs.get(n);
    }

    /**
     * 現在の割り込み線の状態を取得します。
     *
     * 状態の 0 ビット目は割り込み線 0 に、
     * 1 ビット目は割り込み線 1 に、
     * n ビット目は割り込み線 n に、それぞれ対応します。
     *
     * 状態の各ビットには、コアが割り込みを要求していれば 1、
     * そうでなければ 0 が設定されます。
     *
     * @return 割り込み線の状態
     */
    public int getINTCStatus() {
        INTC c;
        int rawInt = 0;

        for (int i = 0; i < MAX_INTCS; i++) {
            c = getINTC(i);

            if (c.isAssert()) {
                rawInt |= 1 << i;
            }
        }

        return rawInt;
    }

    /**
     * IRQ を要求しているコアの状態を取得します。
     *
     * @return IRQ を要求しているコアの状態
     */
    public int getIRQStatus() {
        int st;

        st = getRawHardInt();
        st &= ~intSelect;

        return st;
    }

    /**
     * FIQ を要求しているコアの状態を取得します。
     *
     * @return FIQ を要求しているコアの状態
     */
    public int getFIQStatus() {
        int st;

        st = getRawHardInt();
        st &= intSelect;

        return st;
    }

    /**
     * コアが割り込みを要求または、ソフトウェアから割り込みを要求していて、
     * なおかつ有効な割り込みを取得します。
     *
     * 状態の各ビットには、コアまたはソフトウェアが割り込みを要求していれば 1、
     * そうでなければ 0 が設定されます。
     *
     * @return 有効な割り込みの状態
     */
    public int getRawHardInt() {
        int st;

        st = rawHardInt;
        st |= getINTCStatus();
        st |= rawSoftInt;
        st &= intEnable;

        rawHardInt = st;

        return st;
    }

    @Override
    public boolean tryRead(long addr) {
        return tryAccess(addr);
    }

    @Override
    public boolean tryWrite(long addr) {
        return tryAccess(addr);
    }

    public boolean tryAccess(long addr) {
        int regaddr;

        regaddr = (int)(addr & getAddressMask(LEN_WORD_BITS));

        switch (regaddr) {
        default:
            return super.isValidReg(regaddr);
        }
    }

    @Override
    public int readWord(long addr) {
        int regaddr;
        int result;

        regaddr = (int)(addr & getAddressMask(LEN_WORD_BITS));

        switch (regaddr) {
        case REG_VICIRQSTATUS:
            result = getIRQStatus();
            break;
        case REG_VICFIQSTATUS:
            result = getFIQStatus();
            break;
        case REG_VICRAWINTR:
            result = getRawHardInt();
            break;
        case REG_VICVECTADDR:
            //TODO: not implemented
            System.out.printf("VICVECTADDR: read 0x%08x\n", 0);
            result = 0x0;
            break;
        case REG_VICDEFVECTADDR:
            //TODO: not implemented
            System.out.printf("VICDEFVECTADDR: read 0x%08x\n", 0);
            result = 0x0;
            break;
        default:
            result = super.getReg(regaddr);
            break;
        }

        return result;
    }

    @Override
    public void writeWord(long addr, int data) {
        int regaddr;

        regaddr = (int) (addr & getAddressMask(LEN_WORD_BITS));

        switch (regaddr) {
        case REG_VICIRQSTATUS:
        case REG_VICFIQSTATUS:
        case REG_VICRAWINTR:
            //read only, ignored
            break;
        case REG_VICINTSELECT:
            intSelect = data;
            break;
        case REG_VICINTENABLE:
            intEnable = data;
            break;
        case REG_VICINTENCLEAR:
            rawHardInt &= ~data;
            break;
        case REG_VICSOFTINT:
            rawSoftInt |= data;
            break;
        case REG_VICSOFTINTCLEAR:
            rawSoftInt &= ~data;
            break;
        case REG_VICVECTADDR:
            //TODO: not implemented
            System.out.printf("VICVECTADDR: 0x%08x\n", data);
            break;
        case REG_VICDEFVECTADDR:
            //TODO: not implemented
            System.out.printf("VICDEFVECTADDR: 0x%08x\n", data);
            break;
        case REG_VICITCR:
            //TODO: not implemented
            System.out.printf("VICITCR: 0x%08x\n", data);
            break;
        case REG_VICVECTCNTL0:
        case REG_VICVECTCNTL1:
        case REG_VICVECTCNTL2:
        case REG_VICVECTCNTL3:
        case REG_VICVECTCNTL4:
        case REG_VICVECTCNTL5:
        case REG_VICVECTCNTL6:
        case REG_VICVECTCNTL7:
        case REG_VICVECTCNTL8:
        case REG_VICVECTCNTL9:
        case REG_VICVECTCNTL10:
        case REG_VICVECTCNTL11:
        case REG_VICVECTCNTL12:
        case REG_VICVECTCNTL13:
        case REG_VICVECTCNTL14:
        case REG_VICVECTCNTL15:
            //TODO: not implemented
            System.out.printf("VICVECTCNTL[%d]: 0x%08x\n",
                    (regaddr - REG_VICVECTCNTL0) / 4, data);
            break;
        case REG_VICPERIPHID0:
        case REG_VICPERIPHID1:
        case REG_VICPERIPHID2:
        case REG_VICPERIPHID3:
        case REG_VICPCELLID0:
        case REG_VICPCELLID1:
        case REG_VICPCELLID2:
        case REG_VICPCELLID3:
            //read only, ignored
            break;
        default:
            super.setReg(regaddr, data);
            break;
        }
    }

    public INTC getSubINTCForIRQ() {
        return new SubINTCForIRQ(this);
    }

    public INTC getSubINTCForFIQ() {
        return new SubINTCForFIQ(this);
    }

    public class SubINTCForIRQ implements INTC {
        private PrimaryINTC parent;

        public SubINTCForIRQ(PrimaryINTC c) {
            parent = c;
        }

        @Override
        public boolean isAssert() {
            return parent.getIRQStatus() != 0;
        }

        @Override
        public String getIRQMessage() {
            return "PrimaryINTC IRQ";
        }
    }

    public class SubINTCForFIQ implements INTC {
        private PrimaryINTC parent;

        public SubINTCForFIQ(PrimaryINTC c) {
            parent = c;
        }

        @Override
        public boolean isAssert() {
            return parent.getFIQStatus() != 0;
        }

        @Override
        public String getIRQMessage() {
            return "PrimaryINTC FIQ";
        }
    }
}
