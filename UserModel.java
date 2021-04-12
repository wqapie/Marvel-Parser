package marvel;

import com.opencsv.bean.CsvBindByName;

/**
 * A Java Bean class for reading data
 */
public class UserModel {

    @CsvBindByName
    private String hero;

    @CsvBindByName
    private String book;

    public String getHero() { return this.hero; }
    public void setHero(String v) { this.hero = v; }
    public String getBook() { return this.book; }
    public void setBook(String v) { this.book = v; }
}

