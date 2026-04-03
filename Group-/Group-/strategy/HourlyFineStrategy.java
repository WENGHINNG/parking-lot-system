package strategy;

public class HourlyFineStrategy implements FineStrategy {
    @Override
    public double calculateFine(double hoursOverstayed) {
        if (hoursOverstayed <= 0)
            return 0.0;
        return hoursOverstayed * 20.0;
    }
}