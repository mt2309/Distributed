/**
 * User: mthorpe
 * Date: 10/03/2013
 * Time: 21:57
 */
public class EventuallyPerfectFailureDetector extends AbstractFailureDetector {

    public EventuallyPerfectFailureDetector(Process p) {
        super(p);
        delay = Utils.DELAY;
    }
}
