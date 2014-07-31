package net.katsuster.semu;

/**
 * カラー LCD コントローラ
 *
 * 参考: ARM PrimeCell Color LCD Controller (PL110)
 * ARM DDI0161E
 *
 * @author katsuhiro
 */
public class LCDC extends Controller64Reg32 {
    public static final int REG_LCDTiming0    = 0x00;
    public static final int REG_LCDTiming1    = 0x04;
    public static final int REG_LCDTiming2    = 0x08;
    public static final int REG_LCDTiming3    = 0x0c;
    public static final int REG_LCDUPBASE     = 0x10;
    public static final int REG_LCDLPBASE     = 0x14;
    public static final int REG_LCDIMSC       = 0x18;
    public static final int REG_LCDControl    = 0x1c;
    public static final int REG_LCDRIS        = 0x20;
    public static final int REG_LCDMIS        = 0x24;
    public static final int REG_LCDICR        = 0x28;
    public static final int REG_LCDUPCURR     = 0x2c;
    public static final int REG_LCDLPCURR     = 0x30;

    //0x200-0x3FC: LCDPalette See Color Palette Register

    public static final int REG_CLCDPERIPHID0 = 0xfe0;
    public static final int REG_CLCDPERIPHID1 = 0xfe4;
    public static final int REG_CLCDPERIPHID2 = 0xfe8;
    public static final int REG_CLCDPERIPHID3 = 0xfec;
    public static final int REG_CLCDPCELLID0  = 0xff0;
    public static final int REG_CLCDPCELLID1  = 0xff4;
    public static final int REG_CLCDPCELLID2  = 0xff8;
    public static final int REG_CLCDPCELLID3  = 0xffc;

    public LCDC() {
        //addReg(REG_LCDTiming0, "LCDTiming0", 0x00000000);
        //addReg(REG_LCDTiming1, "LCDTiming1", 0x00000000);
        //addReg(REG_LCDTiming2, "LCDTiming2", 0x00000000);
        //addReg(REG_LCDTiming3, "LCDTiming3", 0x00000);
        //addReg(REG_LCDUPBASE, "LCDUPBASE", 0x0000000);
        //addReg(REG_LCDLPBASE, "LCDLPBASE", 0x00000000);
        //addReg(REG_LCDIMSC, "LCDIMSC", 0x00);
        //addReg(REG_LCDControl, "LCDControl", 0x0000);
        //addReg(REG_LCDRIS, "LCDRIS", 0x00);
        //addReg(REG_LCDMIS, "LCDMIS", 0x00);
        //addReg(REG_LCDICR, "LCDICR", 0x00);
        //addReg(REG_LCDUPCURR, "LCDUPCURR", X);
        //addReg(REG_LCDLPCURR, "LCDLPCURR", X);

        //0x200-0x3FC: LCDPalette See Color Palette Register

        addReg(REG_CLCDPERIPHID0, "CLCDPERIPHID0", 0x10);
        addReg(REG_CLCDPERIPHID1, "CLCDPERIPHID1", 0x11);
        addReg(REG_CLCDPERIPHID2, "CLCDPERIPHID2", 0x04);
        addReg(REG_CLCDPERIPHID3, "CLCDPERIPHID3", 0x00);
        addReg(REG_CLCDPCELLID0, "CLCDPCELLID0", 0x0d);
        addReg(REG_CLCDPCELLID1, "CLCDPCELLID1", 0xf0);
        addReg(REG_CLCDPCELLID2, "CLCDPCELLID2", 0x05);
        addReg(REG_CLCDPCELLID3, "CLCDPCELLID3", 0xb1);
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
        case REG_CLCDPERIPHID0:
        case REG_CLCDPERIPHID1:
        case REG_CLCDPERIPHID2:
        case REG_CLCDPERIPHID3:
        case REG_CLCDPCELLID0:
        case REG_CLCDPCELLID1:
        case REG_CLCDPCELLID2:
        case REG_CLCDPCELLID3:
            //read only, ignored
            break;
        default:
            super.setReg(regaddr, data);
            break;
        }
    }
}
