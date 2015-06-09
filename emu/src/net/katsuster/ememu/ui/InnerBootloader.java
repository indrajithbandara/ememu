package net.katsuster.ememu.ui;

import java.io.*;
import java.net.*;

import net.katsuster.ememu.generic.*;
import net.katsuster.ememu.arm.core.ARMv5;

/**
 * ARM Linux 用の簡易ブートローダです。
 *
 * @author katsuhiro
 */
public class InnerBootloader {
    public static final int ATAG_NONE      = 0x00000000;
    public static final int ATAG_CORE      = 0x54410001;
    public static final int ATAG_MEM       = 0x54410002;
    public static final int ATAG_VIDEOTEXT = 0x54410003;
    public static final int ATAG_RAMDISK   = 0x54410004;
    public static final int ATAG_INITRD2   = 0x54420005;
    public static final int ATAG_SERIAL    = 0x54410006;
    public static final int ATAG_REVISION  = 0x54410007;
    public static final int ATAG_VIDEOLFB  = 0x54410008;
    public static final int ATAG_CMDLINE   = 0x54410009;

    public static int loadURIResource(URI uri, CPU cpu, int addr) {
        int i;

        SystemPane.out.println("loadURL: " + uri.toString());

        try {
            DataInputStream s = new DataInputStream(
                    new BufferedInputStream(uri.toURL().openStream()));

            i = 0;
            try {
                while (true) {
                    cpu.write8_a32(addr + i, s.readByte());
                    i++;
                }
            } catch (EOFException e) {
                //end
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e);
        }

        SystemPane.out.printf("loadURL: '%s' done, %dbytes.\n",
                uri.toString(), i);

        return i;
    }

    public static void bootFromURI(ARMv5 cpu, RAM ramMain, String kimage, String initram, String cmdline) {
        byte[] cmdlb = cmdline.getBytes();
        //+1: need null char at the end of line
        byte[] cmdalign = new byte[(cmdlb.length + 1 + 3) & ~0x3];
        System.arraycopy(cmdlb, 0, cmdalign, 0, cmdlb.length);

        final int addrRAM = 0x00000000;
        final int addrAtagsStart = addrRAM + 0x100;
        int addrAtags = addrAtagsStart;
        final int addrImage = addrRAM + 0x00008000;
        int sizeImage = 0;
        final int addrInitram = addrRAM + 0x00800000;
        int sizeInitram = 0;
        boolean initramExist = !initram.equals("");

        //tentative boot loader for ARM Linux
        try {
            //load Image file
            sizeImage = loadURIResource(new URI(kimage), cpu, addrImage);
            //load initramfs file
            if (initramExist) {
                sizeInitram = loadURIResource(new URI(initram), cpu, addrInitram);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace(System.err);
            return;
        }

        //report address mapping
        SystemPane.out.printf("Address mapping:\n" +
                        "  RAM      : 0x%08x\n" +
                        "  Kernel   : 0x%08x - 0x%08x\n" +
                        "  Initramfs: 0x%08x - 0x%08x\n" +
                        "  ATAGS    : 0x%08x - 0x%08x\n",
                addrRAM, addrImage, addrImage + sizeImage - 1,
                addrInitram, addrInitram + sizeInitram - 1,
                addrAtags, addrAtags + 4096 - 1);

        //r0: 0
        cpu.setReg(0, 0);

        //r1: machine type
        //ARM-Versatile PB
        cpu.setReg(1, 0x00000183);
        //ARM-Versatile AB
        //cpu.setReg(1, 0x0000025e);

        //r2: atags or dtb pointer.
        cpu.setReg(2, addrAtags);
        {
            //ATAG_CORE, size, tag, [flags, pagesize, rootdev]
            cpu.write32_a32(addrAtags + 0x00, 0x00000005);
            cpu.write32_a32(addrAtags + 0x04, ATAG_CORE);
            //bit 0: read only
            cpu.write32(addrAtags + 0x08, 0x00000001);
            cpu.write32(addrAtags + 0x0c, 0x00001000);
            cpu.write32(addrAtags + 0x10, 0x00000000);
            addrAtags += 0x14;

            //ATAG_MEM, size, tag, size, start
            cpu.write32_a32(addrAtags + 0x00, 0x00000004);
            cpu.write32_a32(addrAtags + 0x04, ATAG_MEM);
            cpu.write32_a32(addrAtags + 0x08, ramMain.getSize());
            cpu.write32_a32(addrAtags + 0x0c, addrRAM);
            addrAtags += 0x10;

            //ATAG_INITRD2, size, tag, size, start
            if (initramExist) {
                cpu.write32_a32(addrAtags + 0x00, 0x00000004);
                cpu.write32_a32(addrAtags + 0x04, ATAG_INITRD2);
                cpu.write32_a32(addrAtags + 0x08, addrInitram);
                cpu.write32_a32(addrAtags + 0x0c, sizeInitram);
                addrAtags += 0x10;
            }

            //ATAG_CMDLINE
            cpu.write32_a32(addrAtags + 0x00, 0x00000002 + cmdalign.length / 4);
            cpu.write32_a32(addrAtags + 0x04, ATAG_CMDLINE);
            for (int i = 0; i < cmdalign.length; i++) {
                cpu.write8_a32(addrAtags + 0x08 + i, cmdalign[i]);
            }
            addrAtags += 0x08 + cmdalign.length;

            //ATAG_SERIAL, size, tag, low, high
            cpu.write32_a32(addrAtags + 0x00, 0x00000004);
            cpu.write32_a32(addrAtags + 0x04, ATAG_SERIAL);
            cpu.write32_a32(addrAtags + 0x08, 0x00000020);
            cpu.write32_a32(addrAtags + 0x0c, 0x00000030);
            addrAtags += 0x10;

            //ATAG_REVISION, size, tag, rev
            cpu.write32_a32(addrAtags + 0x00, 0x00000003);
            cpu.write32_a32(addrAtags + 0x04, ATAG_REVISION);
            cpu.write32_a32(addrAtags + 0x08, 0x00000010);
            addrAtags += 0x0c;

            //ATAG_NONE, size, tag
            //It is unique in that its size field in the header
            //should be set to 0 (not 2).
            cpu.write32_a32(addrAtags + 0x00, 0x00000000);
            cpu.write32_a32(addrAtags + 0x04, ATAG_NONE);
            addrAtags += 0x08;
        }

        //pc: entry of stext
        cpu.setPC(addrImage);
        cpu.setJumped(false);
    }
}
