package experiment;

public class SleepAssistant {
    private int counter = 0;
    private final long sleepTimeMS;
    private final long cycleLength;
    private final int sleepsPerCycle;

    public SleepAssistant(double sleepTimeMs) {
        if (Math.abs(0.5-sleepTimeMs) < 0.0001) {
            sleepTimeMS = 1;
            cycleLength = 2;
            sleepsPerCycle = 1;
        }
        else if (Math.abs(0.75-sleepTimeMs) < 0.0001) {
            sleepTimeMS = 1;
            cycleLength = 4;
            sleepsPerCycle = 3;
        }
        else if (sleepTimeMs >= 1.0) {
            if (Math.abs(sleepTimeMs - ((double) ((int) sleepTimeMs))) > 0.0001) { throw new IllegalArgumentException("Sleep time must be an integer."); }

            sleepTimeMS = (long) sleepTimeMs;
            cycleLength = 1;
            sleepsPerCycle = 1;
        }
        else { throw new IllegalArgumentException("Sleep time assumed to be 0.5, 0.75, or positive integer."); }
    }

    public void maybeSleep() {
        counter++;
        if (counter % cycleLength < sleepsPerCycle) {
            try { Thread.sleep(sleepTimeMS); }
            catch (InterruptedException e) { System.out.println("SleepAssistant woke up."); }
        }
    }

}
