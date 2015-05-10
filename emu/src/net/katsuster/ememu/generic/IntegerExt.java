package net.katsuster.ememu.generic;

/**
 * �������Z�̃N���X
 *
 * @author katsuhiro
 */
public class IntegerExt {
    /**
     * �L�����[����������i�����������Z�̉��Z���I�[�o�[�t���[����j���A
     * �ۂ��A���擾���܂��B
     *
     * @param left  ����Z��
     * @param right ���Z���鐔
     * @return �L�����[����������ꍇ�� true�A�������Ȃ��ꍇ�� false
     */
    public static boolean carryFrom(int left, int right) {
        long ll = left & 0xffffffffL;
        long lr = right & 0xffffffffL;

        return ((ll + lr) & ~0xffffffffL) != 0;
    }

    /**
     * �{���[����������i�����������Z�̌��Z���A���_�[�t���[����j���A
     * �ۂ��A���擾���܂��B
     *
     * @param left  �팸�Z��
     * @param right ���Z���鐔
     * @return �L�����[����������ꍇ�� true�A�������Ȃ��ꍇ�� false
     */
    public static boolean borrowFrom(int left, int right) {
        long ll = left & 0xffffffffL;
        long lr = right & 0xffffffffL;

        return lr > ll;
    }

    /**
     * �I�[�o�[�t���[����������i�����t�����Z�̌��ʂ��������ς��j���A
     * �ۂ��A���擾���܂��B
     *
     * @param left  �퉉�Z��
     * @param right ���Z��
     * @param add   ���Z�Ȃ� true�A���Z�Ȃ� false
     * @return �I�[�o�[�t���[�����������Ȃ� true�A�����łȂ���� false
     */
    public static boolean overflowFrom(int left, int right, boolean add) {
        int dest;
        boolean cond1, cond2;

        if (add) {
            //���Z�̏ꍇ
            dest = left + right;

            //left �� right ����������
            cond1 = (left >= 0 && right >= 0) || (left < 0 && right < 0);
            //�Ȃ����� left, right �� dest �̕������قȂ�
            cond2 = (left < 0 && dest >= 0) || (left >= 0 && dest < 0);
        } else {
            //���Z�̏ꍇ
            dest = left - right;

            //left �� right ���قȂ镄��
            cond1 = (left < 0 && right >= 0) || (left >= 0 && right < 0);
            //�Ȃ����� left �� dest �̕������قȂ�
            cond2 = (left < 0 && dest >= 0) || (left >= 0 && dest < 0);
        }

        return cond1 && cond2;
    }
}
