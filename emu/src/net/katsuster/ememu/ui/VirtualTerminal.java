package net.katsuster.ememu.ui;

import java.awt.*;
import java.lang.reflect.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * 仮想端末。
 *
 * @author katsuhiro
 */
public class VirtualTerminal extends JPanel
        implements MouseWheelListener, KeyListener {
    //端末への出力の表示領域パネル
    private VTInnerPane vt;
    //表示領域の右端スクロールバー
    private JScrollBar scr;

    //端末への入力をクラスの外へ渡すためのストリーム
    //Keyboard -> KeyListener -> inPout -> inPin -> (other class)
    private PipedInputStream inPin;
    private PipedOutputStream inPout;

    //端末への出力を表示領域へ渡すためのストリーム
    //(other class) -> outPout -> outPin -> (virtual terminal)
    private PipedInputStream outPin;
    private PipedOutputStream outPout;

    //端末への表示を行うスレッド
    private Thread outDrainer;
    //スレッドを停止させるためのフラグ
    private boolean halted = false;

    //表示を開始する行数
    private int startLine;

    public VirtualTerminal() {
        super(new BorderLayout(), true);

        setFocusable(true);
        addMouseWheelListener(this);
        addKeyListener(this);

        //中央にバイナリデータ表示パネルを配置する
        vt = new VTInnerPane(this);
        vt.setForeground(Color.GRAY);
        vt.setBackground(Color.BLACK);
        vt.setOpaque(false);
        vt.setFont(new Font(Font.MONOSPACED, 0, 12));
        add(vt, BorderLayout.CENTER);

        //右端にスクロールバーを配置する
        scr = new JScrollBar();
        scr.getModel().addChangeListener(vt);
        add(scr, BorderLayout.EAST);

        //入出力用のストリームを作成する
        try {
            inPin = new PipedInputStream();
            inPout = new PipedOutputStream(inPin);

            outPin = new PipedInputStream();
            outPout = new PipedOutputStream(outPin);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e);
        }

        //端末への表示を行うスレッドを作成する
        outDrainer = new Thread(new TextDrainer());
        outDrainer.start();
    }

    /**
     * スクロールバーを取得します。
     *
     * @return スクロールバー
     */
    public JScrollBar getScrollBar() {
        return scr;
    }

    /**
     * 仮想端末への入力を受け取るためのストリームを取得します。
     *
     * @return 仮想端末への入力を受け取るためのストリーム
     */
    public InputStream getInputStream() {
        return inPin;
    }

    /**
     * 仮想端末に出力するためのストリームを取得します。
     *
     * @return 仮想端末に出力するためのストリーム
     */
    public OutputStream getOutputStream() {
        return outPout;
    }

    /**
     * 今すぐスレッドを停止すべきかどうかを取得します。
     */
    public boolean shouldHalt() {
        return halted;
    }

    /**
     * 今すぐスレッドを停止すべきであることを通知します。
     */
    public void halt() {
        halted = true;
    }

    /**
     * 表示を開始する行数を取得します。
     *
     * @return 表示を開始する行数
     */
    public int getStartLine() {
        return startLine;
    }

    /**
     * 表示を開始する行数を設定します。
     *
     * @param l 表示を開始する行数
     */
    public void setStartLine(int l) {
        startLine = l;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        scr.setValue(scr.getValue() + e.getWheelRotation() * 3);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        try {
            inPout.write(e.getKeyChar());
        } catch (IOException ex) {
            //ignored
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        int dat = 0;
        int onmask, offmask;
        boolean shift = false, ctrl = false, alt = false;

        switch (code) {
        case KeyEvent.VK_ALT:
        case KeyEvent.VK_CONTROL:
        case KeyEvent.VK_SHIFT:
            //ignore
            return;
        }

        onmask = KeyEvent.SHIFT_DOWN_MASK;
        offmask = KeyEvent.CTRL_DOWN_MASK | KeyEvent.ALT_DOWN_MASK;
        if ((e.getModifiersEx() & (onmask | offmask)) == onmask) {
            //Shift のみ
            shift = true;
        }

        if (KeyEvent.VK_A <= code && code <= KeyEvent.VK_Z) {
            if (shift) {
                dat = 'A';
            } else {
                dat = 'a';
            }
            dat += code - KeyEvent.VK_A;
        }

        /*try {
            inPout.write(dat);
        } catch (IOException ex) {
            //ignored
        }*/
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //do nothing
    }

    /**
     * outPout に出力された文字を画面に印字するクラスです。
     */
    private class TextDrainer implements Runnable {
        public TextDrainer() {
            //do nothing
        }

        @Override
        public void run() {
            try {
                StringBuffer b = new StringBuffer();

                output:
                while (!shouldHalt()) {
                    b.setLength(0);
                    do {
                        int ch = outPin.read();
                        if (ch == -1) {
                            //EOF
                            break output;
                        }

                        b.append((char) ch);
                    } while (outPin.available() != 0);

                    try {
                        SwingUtilities.invokeAndWait(new StringAppender(b.toString()));
                    } catch (InterruptedException e) {
                        e.printStackTrace(System.err);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace(System.err);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }

        private class StringAppender implements Runnable {
            private String s;

            public StringAppender(String str) {
                s = str;
            }

            @Override
            public void run() {
                vt.append(s);
                //outText.append(s);
            }
        }
    }
}