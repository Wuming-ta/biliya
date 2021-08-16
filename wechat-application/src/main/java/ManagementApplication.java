import com.jfinal.core.JFinal;

public class ManagementApplication {
    public static void main(String[] args) {
        JFinal.start("src/main/webapp", 80, "/",0);
    }
}
