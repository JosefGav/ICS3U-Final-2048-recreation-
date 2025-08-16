public class Utilities {
//    public static void main(String[] args) {
//        System.out.println(compareIntegerArrays(new int[] {1,2,2},new int[] {1,2}));
//    }
    public static int intLen(int num) {
        String numAsStr = num+"";

        int len = numAsStr.length();

        return len;
    }

    public static double log(double base, double arg) {
        return Math.log(arg) / Math.log(base);
    }

    /**
     * Precondition: arrays must be of the same length
     * */
    public static boolean compareIntegerArrays(int[] arr1, int[] arr2) {
        if (arr1.length != arr2.length) return false;

        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i]!=arr2[i]) return false;
        }
        return true;
    }

    public static void printArr ( int[]arr){
        for (int element : arr) {
            System.out.print(element + ", ");
        }
        System.out.println();
    }

    public static int[] getColumn(int[][] arr2D,int col) {
        int[] out = new int[arr2D.length];

        for (int r = 0; r<arr2D.length;r++) {
            out[r]=arr2D[r][col];
        }

        return out;
    }
}
