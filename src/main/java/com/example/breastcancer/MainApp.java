package com.example.breastcancer;

import java.awt.CardLayout;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.google.gson.JsonObject;

import javafx.embed.swing.JFXPanel;
import javafx.application.Platform;
import javafx.scene.Scene;

public class MainApp extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    private final Session session = new Session();
    private final HttpApiClient api = new HttpApiClient();
    private final RiskCalculationService service = new RiskCalculationService(api);
    private final String sessionId = UUID.randomUUID().toString().substring(0, 8);

    private final JFXPanel registerFx = new JFXPanel();
    private final JFXPanel loginFx = new JFXPanel();
    private RiskFormPanel riskPanel;

    public MainApp() {
        // JFrame's constructor because...
        // ...we are extending JFrame
        super("Breast-Cancer ChatBot");

        // Make it look beautiful :)
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820, 640);
        setLocationRelativeTo(null);
        buildScreens();
        // Make it look beautiful :)

        // We assume you are a unregistered user
        // and show the registration panel first.
        cards.show(root, "register");

        /**
         * 
         * Remember that we injected our class with
         * private final JPanel root = new JPanel(cards);
         * 
         * Frame is like a parent - <this>: Class
         * JPanel is like a child - <root>: JPanel
         * JFXPanel is like a grandchild - <registerFx, loginFx>: JFXPanel
         * 
         * Architecture:
         * 
         * A. JFrame (this):
         *      B. JPanel root:
         *          1. JFXPanel registerFx
         *          2. JFXPanel loginFx
         * 
         * 
         */
        add(root);
    }
    /**
     * 
     * Here, root is a swing JPanel that uses CardLayout 
     * to switch between different panels.
     * 
     * It is recommended to use any JFXPanel modifications
     * within Platform.runLater to ensure that
     * you are on the JavaFX Application Thread
     * and avoid concurrency issues because
     * JavaFX and Swing have different threading models.
     * JavaFX has its own "UI thread" which 
     * is responsible for rendering the UI and handling events.
     * 
     */

    /**
     * Swing + JavaFX “Hello, World!” integration (Hello World template)
     *
     * 1. Configure Swing window and add a JFXPanel:
     *    // In your constructor or an init method:
     *    setTitle("Swing + JavaFX Hello World");
     *    setDefaultCloseOperation(EXIT_ON_CLOSE);
     *    setSize(400, 200);
     *    setLocationRelativeTo(null);
     *
     *    // Create and add the JavaFX host panel
     *    fxPanel = new JFXPanel();
     *    add(fxPanel);
     *    setVisible(true);
     *
     * 2. Initialize JavaFX content on the JavaFX thread:
     *    Platform.runLater(() -> {
     *        // Choose ***any*** JavaFX Parent as root: e.g.,
     *        //   StackPane root = new StackPane();
     *        //   VBox     root = new VBox(10);
     *        //   HBox     root = new HBox(10);
     *        //   BorderPane root = new BorderPane();
     *        //   GridPane root = new GridPane();
     *      
     *        However, in our code, we pick a GENERIC PANE:
     *          Pane root = new Pane();
     *          For the actual code, see: RegisterPanel.java, LoginPanel.java
     * 
     *        StackPane root = new StackPane();
     *        root.getChildren().add(new Label("Hello, World!"));
     *
     *        Scene scene = new Scene(root, 400, 200);
     *        fxPanel.setScene(scene);
     *    });
     *
     * 3. Launch everything on the Swing EDT:
     *    public static void main(String[] args) throws Exception {
     *        SwingUtilities.invokeLater(() -> {
     *            try {
     *                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
     *            } catch (Exception ignored) { }
     *
     *            // Instantiate your MainApp (which runs steps 1 & 2)
     *            new MainApp();
     *        });
     *    }
     */

    /*
     * We need to show "loading" state... and this is the only page where
     * we use it in.
     * 
     * Also, we will have a "re-do" button to re-calculate the risk
     * so, either we do "upon-completion" tasks using SwingWorker's done() method
     * 
     * Or we need to do a reset method... the first one is practically better.
     * 
     * Special case for risk calculation (why we have these in MainApp.java):
     *    1. To assure that the UI remains responsive
     *       during long-running tasks like risk calculation,
     *       we use a SwingWorker to perform the calculation in the background.
     *    2. The onRiskCalculationStart() method is called before starting the SwingWorker
     *       to set the loading state of the risk panel.
     *    3. The onRiskCalculationEnd() method is called in the done() method of the SwingWorker
     *       to reset the loading state of the risk panel after the calculation is complete.
     *    4. This ensures that the risk panel shows a loading state while the calculation is in progress,
     *       and it prevents the UI from freezing during the calculation.
     *    5. The risk panel is updated with the results of the risk calculation once it is complete.
     */
    public void onRiskCalculationStart() {
        riskPanel.setLoadingState(true);
    }
    
    public void onRiskCalculationEnd() {
        riskPanel.setLoadingState(false);
    }


    private void buildScreens() {

        root.add(registerFx, "register");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                RegisterController rc = new RegisterController(session, MainApp.this);
                RegisterPanel p = new RegisterPanel(rc);
                /**
                 * REQUIRED TO call setScene, here, on JavaFX Application Thread
                 * to avoid concurrency issues.
                 */
                registerFx.setScene(new Scene(p.getPane()));
            }
        });

        root.add(loginFx, "login");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                LoginController lc = new LoginController(session, MainApp.this);
                LoginPanel p = new LoginPanel(lc);
                /**
                 * REQUIRED TO call setScene, here, on JavaFX Application Thread
                 * to avoid concurrency issues.
                 */
                loginFx.setScene(new Scene(p.getPane()));
            }
        });

        RiskFormController rfc = new RiskFormController(service, session, this);
        riskPanel = new RiskFormPanel(rfc, session);
        root.add(riskPanel, "risk");

    }

    public void onLoginSuccess(String greeting) {

        if (session.getRiskCache().isEmpty()) {
            cards.show(root, "risk");
        } else {
            /**
             * 
             * Similar to how we called RegisterPanel and LoginPanel,
             * we create a new ChatPanel instance here.
             *  
             * However, notice that in RegisterPanel and LoginPanel...
             * ... ***THERE IS NO <extends JFrame>*** in the class declaration
             * ... because they are not JFrame-based panels.
             * ... they are inversely injected with a Pane variable.
             * ... and that Pane variable is equivalent to the JPanel that
             * ... ChatPanel and RiskFormPanel extends.
             * 
             * Since we are using MVC architecture,
             * the controllers are responsible for managing the views.
             * 
             * We follow SOLID principles here... which is why
             * we have too many classes to introduce encapsulation
             * and abstraction.
             * 
             * While ChatController does not have much responsibility
             * in this example, it is a good practice to keep
             * the controller separate from the view.
             * 
             */
            ChatPanel chatP = new ChatPanel(api, sessionId, session.getRiskCache(), greeting, this);
            root.add(chatP, "chat");
            cards.show(root, "chat");
        }
    }

    public void onRiskSuccess(JsonObject merged, String greeting) {
        session.addRisk(merged);
        /**
         * <merged> is a JsonObject that contains the risk assessment
         * you dont need to convert it so the Session.addRisk(merged)
         * just adds it to the risk cache directly:
         *    It contains:
         *      public void addRisk(JsonObject j) {
         *          riskCache.add(j);
         *          saveRiskToDb(j);
         *      }
         *    Where riskCache is a List<JsonObject>, and it stores all the risk assessments
         *    for the current user session.
         *    -> see Session.java for details.
         */
        ChatPanel chatP = new ChatPanel(api, sessionId, session.getRiskCache(), greeting, this);
        root.add(chatP, "chat");
        cards.show(root, "chat");
    }

    /**
     * We create an object of this class.
     * Future Work: consider Singelton pattern
     * to avoid creating multiple instances of MainApp.
     * 
     * A standard practice that fits well with the given design, we used....
     *   1. Injecting MainApp into controllers
     *   2. Defined methods to switch panels within MainApp using CardLayout
     *          a. CardLayout <cards> is used to switch between different panels and is only defined once here.
     *          b. The methods showLoginPanel(), showRegisterPanel(), showRiskPanel(), and logout() are used to switch between panels.
     *   3. We call these methods from the controllers to switch panels.
     *          a. For example, in LoginController.java, we call controller.showRegisterPanel() to switch to the registration panel.
     *          b. In RegisterController.java, we call controller.showLoginPanel() to switch to the login panel.
     *          c. In RiskFormController.java, we call controller.showLoginPanel() to switch to the login panel after successful risk calculation.
     *          d. In ChatController.java, we call controller.logout() to switch to the login panel after logout.
     *          e. In ChatController.java, we call controller.showRiskPanel() to switch to the risk panel after successful risk calculation.
     *          f. In RiskFormController.java, we call controller.logout() to switch to the login panel after logout.
     */
    public void showLoginPanel() {
        cards.show(root, "login");
    }

    public void showRegisterPanel() {
        cards.show(root, "register");
    }

    public void showRiskPanel() {
        cards.show(root, "risk");
    }

    public void logout() {
        session.logout();
        cards.show(root, "login");
    }

    /**
     * Lifecycle:
     *  1. The main method is the entry point of the application.
     *  2. It sets the Look and Feel to the system default.
     *  3. It uses SwingUtilities.invokeLater to ensure that the UI is created on the Event Dispatch Thread (EDT).
     *  4. A new instance of MainApp is created, which sets up the JFrame and initializes the panels.
     *  5. The JFrame is made visible, displaying the initial panel (registration panel).
     */
    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.invokeLater(new Runnable() {
            /**
             * This is just for thread safety.
             * There is no magical "functions we define in our class"
             * that will be called automatically by overriding the run() method.
             * 
             * This is just a standard practice to ensure that
             * the UI is created on the Event Dispatch Thread (EDT).
             * 
             * We are creating an instance of MainApp
             * which will set up the JFrame and initialize the panels.
             * then the main line that starts the UI and UX is:
             *            >>> cards.show(root, "register");
             * 
             * When is this invoked?
             *            Below, there is a line that says:
             *                     >>> new MainApp().setVisible(true);
             *            The <cards.show(root, "register");> is invoked during the
             *                     >>> new MainApp()
             *            part of:
             *                     >>> new MainApp().setVisible(true);
             * 
             * Remember, buildScreens() are just built to get the panels ready.
             * 
             * Notice how only ONE CLASS in our files has a constructor that
             *         1. extends JFrame
             *         2. has a constructor that has CardsLayout variable
             *         3. calls a cards.show() WITHIN THE CONSTRUCTOR ITSELF!!!!
             * ... and that is this MainApp class!!!
             * 
             * And what this means is when we create a MainApp (a JFrame),
             * we will never SHIP IT WITHOUT an integrated CardLayout
             * that can switch between different pre-defined panels.
             * 
             * Those panels are:
             *        1. RegisterPanel (Technically a JFXPanel where we have a Pane)
             *        2. LoginPanel (Technically a JFXPanel where we have a Pane)
             *        3. RiskFormPanel
             *        4. ChatPanel
             *
             * Note: Each Panel class returns either a...
             * ...JavaFX Pane (files: RegisterPanel.java, LoginPanel.java)
             * ...or is itself a Swing JPanel (files: RiskFormPanel.java, ChatPanel.java).
             * 
             * {JFXPanel | JavaFX Pane}, we return because, with that, we do:
             *            registerFx.setScene(new Scene(p.getPane()));
             *            loginFx.setScene(new Scene(p.getPane()));
             *            ...where p.getPane() is a JavaFX Pane.
             * 
             *            <<<vs>>>
             * 
             * {JPanel}, we do not need to return really, we just create...
             * ...a new instance of RiskFormPanel/ChatPanel and add it to the root JPanel.
             * 
             */
            @Override
            public void run() {
                MainApp app = new MainApp();
                app.setVisible(true);
                /**
                 * 
                 * These links are clickable in most platforms including vscode:
                 * 
                 * Case (1): RegisterPanel -> see <RegisterPanel.java> and <RegisterController.java>
                 * Case (2): LoginPanel -> see <LoginPanel.java> and <LoginController.java>
                 * Case (3): RiskFormPanel -> see <RiskFormPanel.java>, <RiskFormController.java>, <RiskInput.java>, <RiskCalculationService.java>, <RiskResponseMapper.java>, <RiskResult.java>
                 * Case (4): ChatPanel -> see <ChatPanel.java> and <ChatController.java>
                 * 
                 * For all cases together, we have <Session.java>, <HttpApiClient.java>, <Credentials.java>
                 * ... that work together to facilitate communication between this application
                 * ... and the backend server (Flask server).
                 * 
                 * As you will see, Session.java follows the Singleton pattern
                 * ... to ensure that there is only one instance of the session
                 * ... throughout the application.
                 * ... for this -> see <Session.java>, <HttpApiClient.java>, <Credentials.java>
                 */
            }
        });
    }
}