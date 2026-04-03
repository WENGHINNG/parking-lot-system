package strategy;

public class ProgressiveFineStrategy implements FineStrategy {
    private static final long serialVersionUID = 1L;

    @Override
    public double calculateFine(double hoursOverstayed) {
        if (hoursOverstayed <= 0)
            return 0.0;

        // 逻辑：累加制 (Cumulative)
        // First 24h: 50
        // 24-48h: +100 (Total 150)
        // 48-72h: +150 (Total 300)
        // >72h: +200 (Total 500)

        if (hoursOverstayed <= 24) {
            return 50.0;
        } else if (hoursOverstayed <= 48) {
            return 50.0 + 100.0; // 150
        } else if (hoursOverstayed <= 72) {
            return 50.0 + 100.0 + 150.0; // 300
        } else {
            return 50.0 + 100.0 + 150.0 + 200.0; // 500
        }
    }
}