import java.util.Arrays;

/**
 * Класс открытого текста с возможностью шифрования
 */
public class PlainText extends FeistelCipher {
    private String plainText;

    /**
     * Конструктор - создание нового объекта
     */
    public PlainText(String plainText) {
        this.plainText = plainText;
    }

    /**
     * Шифрует каждый блок из {@link #BLOCK_SIZE} символов в строке по порядку.
     * Если длина открытого текста не делится на блоки одинаковой длины,
     * то выполняется {@link #addSpaces()}
     *
     * @return зашифрованную строку
     */
    public String encryptText() {
        if (plainText.length() % (BLOCK_SIZE) != 0) {
            addSpaces();
        }
        StringBuilder sb = new StringBuilder();
        int beginIndex = 0;
        int endIndex = beginIndex + BLOCK_SIZE / 2;
        /*цикл для получение подстрок равных по длине в половину блока, которые делятся на левую и правую части,
        передаем left и right в метод encryptText(left, right), который вернет нам объедененный массив из зашифрованных left и right*/
        for (int i = BLOCK_SIZE - 1; i < plainText.length(); i += BLOCK_SIZE) {
            int[] left = plainText.substring(beginIndex, endIndex).codePoints().toArray();
            int[] right = plainText.substring(endIndex, i + 1).codePoints().toArray();
            Arrays.stream(encryptText(left, right)).forEach(value -> sb.append((char) value));
            beginIndex = i + 1;
            endIndex = beginIndex + BLOCK_SIZE / 2;
        }
        return sb.toString();
    }

    /**
     * Функция шифрования по алгоритму Фейстеля
     *
     * @param left  массив кодированных символов левой части блока
     * @param right массив кодированных символов правой части блока
     * @return массив зашифрованных символов из левой и правой части блока
     */
    private int[] encryptText(int[] left, int[] right) {
        for (int key : KEYS) {
            int[] temp = left;
            left = right;
            right = temp;
            for (int j = 0; j < left.length; j++) {
                right[j] = temp[j] ^ secretFunc(key, left[RESHUFFLE[j] - 1]);
            }
        }
        int[] encryptedPart = new int[BLOCK_SIZE];
        System.arraycopy(left, 0, encryptedPart, 0, BLOCK_SIZE / 2);
        System.arraycopy(right, 0, encryptedPart, BLOCK_SIZE / 2, BLOCK_SIZE / 2);
        return encryptedPart;
    }


    /**
     * Метод, который добавляет недостающее кол-во символов, для корректной работы алгоритма, при дешифровке эти символы удалятся
     * <p>
     * Пример:<p>
     * <blockquote><pre>
     * "это не понадобится".length() == 18
     * после выполнения метода будет выглядеть так:
     * "это не понадобится      ".length() == 24
     * </pre><blockquote>
     */
    private void addSpaces() {
        int startPlainTextLength = plainText.length();
        int neededPlainTextLength = BLOCK_SIZE * (startPlainTextLength / BLOCK_SIZE + 1);
        while (plainText.length() < neededPlainTextLength) {
            plainText += " ";
        }
    }
}