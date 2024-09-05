package vn.vti.clothing_shop.constants;

public class RegularExpression {
    public static final String EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    public static final String PHONE_NUMBER = "(?:([+]\\d{1,4})[-.\\s]?)?(?:[(](\\d{1,3})[)][-.\\s]?)?(\\d{1,4})[-.\\s]?(\\d{1,4})[-.\\s]?(\\d{1,9})";
    public static final String NUMBER = "^[0-9]*$|^[0-9]*\\.[0-9]*$";
    public static final String INTEGER = "^[0-9]*$";
    public static final String STAR_RATING = "^[0-5]$|^[0-4]\\.[0-9]$|^5\\.0$";
    public static final String COLOR = "^#([a-fA-F0-9]{6})$";
    public static final String BOOLEAN = "true|false";
}
