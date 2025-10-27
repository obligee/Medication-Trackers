package core;

import java.util.List;

//UC1 Validator(Make sure that all answers are full and not left blank)
public final class NotificationValidator {

    private NotificationValidator() { /* utility class */ }
    public static boolean allSelectedYesOrNo(List<String> selections) {
        if (selections==null || selections.isEmpty()) return false;
        for (String s:selections) {
            if (s==null) return false;
            String v=s.trim().toLowerCase();
            if (!v.equals("yes") && !v.equals("no")) return false;
        }
        return true;
    }
}