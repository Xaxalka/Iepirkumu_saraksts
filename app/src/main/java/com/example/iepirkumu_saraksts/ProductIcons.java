package com.example.iepirkumu_saraksts;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/** Picks an emoji "picture" for a product by name keyword, falling back to its category. */
public final class ProductIcons {
    private static final Map<String, String> KEYWORDS = new LinkedHashMap<>();

    static {
        // Latvian + Russian + English keywords (matched as substrings, lowercase)
        put("🥛", "piens", "молок", "milk");
        put("🍞", "maize", "bulciņ", "хлеб", "bread");
        put("🧀", "siers", "сыр", "cheese");
        put("🥚", "olas", "ola ", "яйц", "egg");
        put("🧈", "sviests", "масл", "butter");
        put("🍎", "ābol", "яблок", "apple");
        put("🍌", "banān", "банан", "banana");
        put("🍊", "apelsīn", "апельсин", "orange", "mandarīn");
        put("🍋", "citron", "лимон", "lemon");
        put("🍇", "vīnog", "виноград", "grape");
        put("🍓", "zemen", "клубник", "strawberr");
        put("🍉", "arbūz", "арбуз");
        put("🍅", "tomāt", "помидор", "томат", "tomato");
        put("🥕", "burkān", "морков", "carrot");
        put("🥔", "kartup", "картоф", "potato");
        put("🧅", "sīpol", "лук", "onion");
        put("🥒", "gurķ", "огур", "cucumber");
        put("🥬", "kāpost", "салат", "капуст", "salad", "cabbage");
        put("🍗", "vistas", "курица", "куриц", "chicken");
        put("🥩", "gaļ", "мясо", "meat", "steak");
        put("🐟", "zivs", "zivis", "рыба", "fish");
        put("🍚", "rīsi", "рис", "rice");
        put("🍝", "makaron", "makoron", "spageti", "nūdel", "лапша", "спагетти", "noodle", "макарон", "pasta");
        put("🥣", "putra", "auzu", "каша", "овсян");
        put("🍬", "konfek", "cukur", "конфет", "сахар", "sugar", "candy");
        put("🍫", "šokolād", "шоколад", "chocolate");
        put("🍪", "cepum", "печень", "cookie");
        put("🍕", "pica", "пицц", "pizza");
        put("☕", "kafij", "кофе", "coffee");
        put("🍵", "tēja", "чай", "tea");
        put("🧃", "sula", "сок", "juice");
        put("💧", "ūdens", "вода", "water");
        put("🍺", "alus", "пиво", "beer");
        put("🍷", "vīns", "вино", "wine");
        put("🥤", "kola", "кола", "cola", "limonād", "лимонад");
        put("🧻", "papīrs", "tualetes", "бумаг", "paper");
        put("🧼", "ziepes", "мыло", "soap", "šampūn", "шампун");
        put("🪥", "zobu", "зуб", "tooth");
        put("🧴", "krēms", "крем", "gēls", "гель");
        put("🧽", "sūklis", "губк", "sponge");
        put("🧹", "slota", "швабр", "веник");
        put("🔋", "baterij", "батарей", "battery");
        put("💡", "spuldz", "лампоч", "bulb");
        put("🥫", "konserv", "консерв");
        put("🍯", "medus", "мёд", "мед", "honey");
        // more everyday products and brands
        put("🥛", "kefīr", "jogurt", "biezpiens", "krējum", "кефир", "йогурт", "творог", "сметан", "yogurt");
        put("🌭", "desa", "desiņ", "cīsiņ", "šķiņķ", "speķ", "колбас", "сосиск", "sausage", "ham");
        put("🥓", "bekon", "бекон", "bacon");
        put("🥩", "cūkgaļ", "liellopu", "malta", "свинин", "говяд", "фарш");
        put("🍤", "garneles", "креветк", "shrimp");
        put("🥑", "avokād", "авокадо", "avocado");
        put("🥝", "kivi", "киви");
        put("🍐", "bumbier", "груш", "pear");
        put("🍑", "persik", "plūm", "персик", "слив", "peach");
        put("🍒", "ķirš", "вишн", "черешн", "cherry");
        put("🫐", "mellen", "avenes", "голубик", "малин", "berries");
        put("🍍", "ananas", "ананас", "pineapple");
        put("🥦", "brokoļ", "брокколи", "broccoli");
        put("🌽", "kukurūz", "кукуруз", "corn");
        put("🫑", "paprik", "перец", "pepper");
        put("🧄", "ķiploks", "ķiploki", "чеснок", "garlic");
        put("🥬", "zaļum", "dilles", "pētersīļ", "зелень", "укроп", "петрушк", "spinat", "špinā");
        put("🍄", "sēnes", "šampinjon", "гриб", "mushroom");
        put("🫘", "pupiņ", "zirņ", "lēcas", "фасол", "горох", "чечевиц", "beans");
        put("🌾", "milti", "мука", "flour");
        put("🫒", "eļļa", "масло раст", "olive");
        put("🧂", "sāls", "соль", "salt");
        put("🥫", "kečup", "majonēz", "mērce", "кетчуп", "майонез", "соус", "sauce");
        put("🍿", "čipsi", "popkorn", "чипсы", "chips");
        put("🥜", "rieksti", "riekst", "орех", "nuts");
        put("🍦", "saldējum", "мороженое", "ice cream");
        put("🍰", "kūka", "torte", "торт", "пирог", "cake");
        put("🥟", "pelmeņ", "пельмен");
        put("🍲", "zupa", "суп", "soup");
        put("🍩", "virtulis", "donut", "пончик");
        put("🥐", "kruasān", "круассан", "croissant");
        put("🍔", "burger", "бургер");
        put("🥤", "pepsi", "fanta", "sprite", "coca", "cola", "фанта", "спрайт", "газиров", "gāzēt", "enerģij", "energy");
        put("💧", "minerāl", "минерал");
        put("🍾", "šampanie", "šampan", "шампанск", "champagne", "prosecco");
        put("🥃", "viskijs", "vodka", "degvīn", "konjak", "виски", "водка", "коньяк", "rums", "whisk");
        put("☕", "kakao", "какао", "latte", "капучино", "espresso");
        put("🧴", "dezodor", "дезодор", "losjon", "лосьон", "matu", "волос", "mazgājam", "gel");
        put("🧺", "veļas", "pulveris", "стирал", "порошок", "mazgāšanas līdz", "kondicioniер");
        put("🍽️", "trauku", "посуд");
        put("🧻", "salvet", "салфет", "dvieļ", "полотенц", "tissue");
        put("🛍️", "maisiņ", "maisi", "пакет", "bags");
        put("🪒", "žilet", "skuveklis", "бритв", "razor");
        put("🩹", "plāksteris", "пластырь", "tablet", "vitamīn", "витамин", "aptieci");
        put("🧤", "cimdi", "cepure", "zeķes", "перчатк", "носки", "шапк", "socks");
        put("🐕", "suņu", "kaķu", "корм", "pet food");
    }

    private static final java.util.Set<String> DRINKS =
            new java.util.HashSet<>(java.util.Arrays.asList("☕", "🍵", "🧃", "💧", "🍺", "🍷", "🥤", "🍾", "🥃"));
    private static final java.util.Set<String> HOUSEHOLD =
            new java.util.HashSet<>(java.util.Arrays.asList("🧻", "🧼", "🪥", "🧴", "🧽", "🧹", "🔋", "💡", "🧺", "🛍️", "🪒", "🩹", "🐕", "🧤"));

    private ProductIcons() {}

    private static void put(String emoji, String... keywords) {
        for (String k : keywords) KEYWORDS.put(normalize(k), emoji);
    }

    /** Lowercase and strip diacritics so "ūdens" and "udens" (or "Ābols"/"abols") both match. */
    private static String normalize(String s) {
        return Normalizer.normalize(s.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    /** @return the emoji whose keyword occurs in the name, or null for an unknown product */
    private static String emojiFor(String name) {
        if (name == null) return null;
        String lower = normalize(name);
        for (Map.Entry<String, String> e : KEYWORDS.entrySet()) {
            if (lower.contains(e.getKey())) return e.getValue();
        }
        return null;
    }

    /**
     * Guesses the category (Latvian name as stored in Product.category) of a well-known product,
     * or returns null if the name is not recognised.
     */
    public static String categoryFor(String name) {
        String emoji = emojiFor(name);
        if (emoji == null) return null;
        if (DRINKS.contains(emoji)) return "Dzērieni";
        if (HOUSEHOLD.contains(emoji)) return "Sadzīve";
        return "Pārtika";
    }

    public static String forProduct(Product product) {
        String emoji = emojiFor(product.name);
        if (emoji != null) return emoji;
        String cat = product.category == null ? "" : product.category;
        switch (cat) {
            case "Pārtika": return "🍽️";
            case "Dzērieni": return "🥤";
            case "Sadzīve": return "🏠";
            default: return "🛒";
        }
    }
}
