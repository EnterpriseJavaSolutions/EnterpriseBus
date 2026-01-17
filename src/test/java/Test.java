import com.github.EnterpriseJavaSolutions.EnterpriseBus.EventBus;
import com.github.EnterpriseJavaSolutions.EnterpriseBus.annotation.Subscribe;

// The expected output is:
// ExampleEvent received!
public class Test {
    public static final Test instance = new Test();

    public EventBus eventBus = new EventBus();

    public static void main(String[] args) {
        // Testing event bus without any subscribers
        instance.eventBus.post(new ExampleEvent());
        // Testing registrations
        instance.eventBus.register(instance);
        // Testing event bus with a subscriber
        instance.eventBus.post(new ExampleEvent());
    }

    // You can choose any name for the listener method, but it is recommended to use the event type prefixed with "on"
    // It must have the "@Subscribe" annotation and accept a single parameter of the event type
    @Subscribe
    public void onExample(ExampleEvent event) {
        System.out.println("ExampleEvent received!");
    }
}
