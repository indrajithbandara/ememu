package net.katsuster.semu;

import java.util.*;

/**
 * コプロセッサ。
 *
 * @author katsuhiro
 */
public class CoProc {
    private int no;
    private CPU proc;
    private Map<Integer, Register> cregs;

    public CoProc(int no, CPU proc) {
        this.no = no;
        this.proc = proc;
        this.cregs = new HashMap<Integer, Register>();
    }

    /**
     * コプロセッサ番号を取得します。
     *
     * @return コプロセッサ番号
     */
    public int getNumber() {
        return no;
    }

    /**
     * コプロセッサレジスタの定義を追加します。
     *
     * @param cn   コプロセッサレジスタ番号
     * @param name レジスタ名
     */
    public void addCReg(int cn, String name) {
        cregs.put(cn, new Register(name, 0));
    }

    /**
     * コプロセッサレジスタの定義を追加します。
     *
     * @param cn   コプロセッサレジスタ番号
     * @param name レジスタ名
     * @param val  レジスタの初期値
     */
    public void addCReg(int cn, String name, int val) {
        cregs.put(cn, new Register(name, val));
    }

    /**
     * コプロセッサレジスタ番号を取得します。
     *
     * レジスタ番号は、4ビットずつのフィールドに分かれています。
     * ビット [15:12]: CRn
     * ビット [11:8]: opcode_1
     * ビット [7:4]: CRm
     * ビット [3:0]: opcode_2
     * を指定します。
     *
     * @param crn
     * @param opcode1
     * @param crm
     * @param opcode2
     * @return
     */
    public static int getCRegID(int crn, int opcode1, int crm, int opcode2) {
        return ((crn & 0x0f) << 12) |
                ((opcode1 & 0x07) << 8) |
                ((crm & 0x0f) << 4) |
                ((opcode2 & 0x07) << 0);
    }

    /**
     * コプロセッサレジスタ番号が有効かどうかを取得します。
     *
     * @param cn コプロセッサレジスタ番号
     * @return 指定したレジスタが存在するなら true、存在しなければ false
     */
    public boolean validCRegNumber(int cn) {
        return cregs.containsKey(cn);
    }

    /**
     * コプロセッサレジスタの値を取得します。
     *
     * レジスタ番号は、4ビットずつのフィールドに分かれています。
     * ビット [15:12]: CRn
     * ビット [11:8]: opcode_1
     * ビット [7:4]: CRm
     * ビット [3:0]: opcode_2
     * を指定します。
     *
     * @param cn コプロセッサレジスタ番号
     * @return レジスタの値
     */
    public int getCReg(int cn) {
        Register r;

        r = cregs.get(cn);
        if (r == null) {
            throw new IllegalStateException(String.format(
                    "Illegal coproc %d reg(%08x).", getNumber(), cn));
        }

        return r.getValue();
    }

    /**
     * コプロセッサレジスタの値を設定します。
     *
     * レジスタ番号は、4ビットずつのフィールドに分かれています。
     * ビット [15:12]: CRn
     * ビット [11:8]: opcode_1
     * ビット [7:4]: CRm
     * ビット [3:0]: opcode_2
     * を指定します。
     *
     * @param cn   コプロセッサレジスタ番号
     * @param val 新しいレジスタの値
     */
    public void setCReg(int cn, int val) {
        Register r;

        r = cregs.get(cn);
        if (r == null) {
            throw new IllegalStateException(String.format(
                    "Illegal coproc %d reg(%08x).", getNumber(), cn));
        }

        r.setValue(val);
    }

    @Override
    public String toString() {
        return String.format("cp%d", getNumber());
    }
}