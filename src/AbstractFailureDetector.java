import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * User: mthorpe
 * Date: 10/03/2013
 * Time: 15:32
 */
abstract class AbstractFailureDetector implements IFailureDetector {

    Process p;
    LinkedList<Integer> suspects;
    LinkedList<Integer> seen;
    Timer t;

    static final int SECOND = 1000;
    int count = 0;

    final class PeriodicTask extends TimerTask {
        public void run() {
            p.broadcast("heartbeat","null");

            // Every 3 average delays
            if ((count + 1) % delay() == 0) {

                for (int i = 0; i < p.getNo(); i++) {
                    if (!seen.contains(i)) {
                        suspects.add(i);
                    }
                }
                resetProcessArray();
            }

            count++;
        }
    }

    public AbstractFailureDetector(Process p) {
        this.p = p;
        this.t = new Timer();
        suspects = new LinkedList<>();
        resetProcessArray();
    }

    // Fill the process array with true values
    private void resetProcessArray() {
        seen = new LinkedList<>();
    }

    @Override
    public void begin() {
        t.schedule(new PeriodicTask(),0 , SECOND);
    }

    @Override
    public void receive(Message m) {
        // given that we passed this message through from the process iff it was a heartbeat
        int source = m.getSource();
        int averageDelay = Utils.DELAY;

        // If we suspect this process and we get a heartbeat, its no longer suspect
        if (suspects.contains(source)) {
            suspects.remove(new Integer(source));
        }

        seen.add(source);

        Utils.out(p.pid,m.toString());
    }

    @Override
    public boolean isSuspect(Integer process) {
        return suspects.contains(process);
    }

    @Override
    public int getLeader() {
        return -1;
    }

    @Override
    public void isSuspected(Integer process) {

    }

    abstract int delay();
}
