package ras.utils;

public class ProbabilityUtils {

    private final static int MAX_PONDERATION = 10;
    private final static int MEDIUM_PONDERATION = 5;
    private final static int MIN_PONDERATION = 1;


    public static int weigh(float numResultado) {
        if (numResultado < 10)
            return MAX_PONDERATION;
        else if (numResultado > 10 && numResultado < 20)
            return MEDIUM_PONDERATION;
        else
            return MIN_PONDERATION;
    }

}
