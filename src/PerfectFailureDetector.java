/**
 * User: mthorpe
 * Date: 10/03/2013
 * Time: 21:55
 */
public class PerfectFailureDetector extends AbstractFailureDetector {

    public PerfectFailureDetector(Process p) {
        super(p);
        delay = Utils.DELAY*3;
    }
}
