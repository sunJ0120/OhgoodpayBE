package com.ohgoodteam.ohgoodpay.pay.enums;


public enum GradePointRate {
    BRONZE("Bronze", 0.002),
    SILVER("Silver", 0.006),
    GOLD("Gold", 0.01),
    PLATINUM("Platinum", 0.015),
    DIAMOND("Diamond", 0.02);

    private final String gradeName;
    private final double rate;

    GradePointRate(String gradeName, double rate) {
        this.gradeName = gradeName;
        this.rate = rate;
    }

    public String getGradeName() { return gradeName; }
    public double getRate() { return rate; }

    public static double getRateByGrade(String gradeName) {
        for (GradePointRate g : values()) {
            if (g.getGradeName().equalsIgnoreCase(gradeName)) {
                return g.getRate();
            }
        }
        return 0.0;
    }
}


