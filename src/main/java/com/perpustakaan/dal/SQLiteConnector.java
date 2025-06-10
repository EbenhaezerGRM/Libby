package com.perpustakaan.dal;

import com.perpustakaan.util.PasswordUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteConnector implements DatabaseConnector {
    private static final String DB_URL = "jdbc:sqlite:Libby.db";
    private Connection connection;

    public SQLiteConnector() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver SQLite JDBC tidak ditemukan. Pastikan sudah ada di dependency Maven.");
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            System.err.println("Gagal membuat koneksi ke database.");
            e.printStackTrace();
        }
        return connection;
    }
    
    public void initializeDatabase() {
        String[] queries = {
            "PRAGMA foreign_keys = ON;",
            
            "CREATE TABLE IF NOT EXISTS books ("
                    + "id TEXT PRIMARY KEY NOT NULL,"
                    + "title TEXT NOT NULL,"
                    + "author TEXT NOT NULL,"
                    + "publicationYear INTEGER,"
                    + "description TEXT,"
                    + "category TEXT NOT NULL,"
                    + "total_stock INTEGER NOT NULL DEFAULT 1,"
                    + "available_stock INTEGER NOT NULL DEFAULT 1);",

            "CREATE TABLE IF NOT EXISTS users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "username TEXT NOT NULL UNIQUE,"
                    + "password TEXT NOT NULL,"
                    + "role TEXT NOT NULL,"
                    + "status TEXT NOT NULL DEFAULT 'ACTIVE'," 
                    + "total_fines_unpaid REAL DEFAULT 0.0);",

            "CREATE TABLE IF NOT EXISTS transactions ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "book_id TEXT NOT NULL,"
                    + "user_id INTEGER NOT NULL,"
                    + "borrow_time TEXT NOT NULL,"
                    + "return_time TEXT,"
                    + "due_date TEXT NOT NULL," 
                    + "fine_amount REAL DEFAULT 0.0," 
                    + "FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE RESTRICT,"
                    + "FOREIGN KEY (user_id) REFERENCES users(id));",
            
            "CREATE TABLE IF NOT EXISTS reviews ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "book_id TEXT NOT NULL,"
                    + "user_id INTEGER NOT NULL,"
                    + "rating INTEGER NOT NULL,"
                    + "comment TEXT,"
                    + "review_date TEXT NOT NULL,"
                    + "UNIQUE(book_id, user_id)," 
                    + "FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,"
                    + "FOREIGN KEY (user_id) REFERENCES users(id));",

            "CREATE TABLE IF NOT EXISTS reservations ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "book_id TEXT NOT NULL,"
                    + "user_id INTEGER NOT NULL,"
                    + "reservation_date TEXT NOT NULL,"
                    + "status TEXT NOT NULL DEFAULT 'ACTIVE'," 
                    + "FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,"
                    + "FOREIGN KEY (user_id) REFERENCES users(id));"
        };
        
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            for (String sql : queries) {
                stmt.execute(sql);
            }
            insertInitialData(stmt);
        } catch (SQLException e) {
            System.err.println("Gagal menginisialisasi skema database.");
            e.printStackTrace();
        }
    }
    
    private void insertInitialData(Statement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery("SELECT id FROM users LIMIT 1;")) {
            if (!rs.next()) {
                System.out.println("Memasukkan data user awal...");
                stmt.execute("INSERT INTO users (username, password, role) VALUES ('admin', '" + PasswordUtil.hashPassword("admin123") + "', 'ADMIN');");
                stmt.execute("INSERT INTO users (username, password, role) VALUES ('ebenhaezer', '" + PasswordUtil.hashPassword("ebenhaezer123") + "', 'ADMIN');");
                stmt.execute("INSERT INTO users (username, password, role) VALUES ('wening', '" + PasswordUtil.hashPassword("wening123") + "', 'ADMIN');");
            }
        }
        
        try (ResultSet rs = stmt.executeQuery("SELECT id FROM books LIMIT 1;")) {
             if (!rs.next()) {
                System.out.println("Memasukkan data buku awal...");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1001SC', 'The Emperor of All Maladies', 'Yuval Noah Harari', 2005, 'An in-depth analysis of the human condition and progress.', 'Science', 1, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1002SC', 'The Tell-Tale Brain', 'Yuval Noah Harari', 2012, 'A masterfully written account of scientific discoveries.', 'Science', 5, 5);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1003SC', 'Life on the Edge', 'Jonah Lehrer', 1983, 'A blend of storytelling and science, making learning enjoyable.', 'Science', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1004SC', 'The Tell-Tale Brain', 'David Brooks', 1994, 'A revolutionary take on scientific concepts explained clearly.', 'Science', 3, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1005SC', 'Physics of the Impossible', 'Daniel Kahneman', 1993, 'Thought-provoking and thoroughly researched narrative.', 'Science', 1, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1006SO', 'Drive', 'Angela Duckworth', 1995, 'Highlights key moments in scientific and social development.', 'Social', 3, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1007SO', 'Quiet', 'Daniel Kahneman', 1998, 'Challenges conventional thinking with new perspectives.', 'Social', 5, 5);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1008SC', 'The Grand Design', 'Michio Kaku', 1996, 'An in-depth analysis of the human condition and progress.', 'Science', 1, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1009SO', 'Capital in the Twenty-First Century', 'Steven Pinker', 1977, 'An in-depth analysis of the human condition and progress.', 'Social', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1010SO', 'Originals', 'Steven Pinker', 1995, 'An in-depth analysis of the human condition and progress.', 'Social', 4, 4);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1011SO', 'The Better Angels of Our Nature', 'Angela Duckworth', 1993, 'Thought-provoking and thoroughly researched narrative.', 'Social', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1012SC', 'Silent Spring', 'Jane Goodall', 1984, 'A revolutionary take on scientific concepts explained clearly.', 'Science', 3, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1013SO', 'Influence', 'Jonah Lehrer', 1982, 'A revolutionary take on scientific concepts explained clearly.', 'Social', 5, 5);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1014SC', 'The Origin of Species', 'Oliver Sacks', 1970, 'Highlights key moments in scientific and social development.', 'Science', 3, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1015SO', 'Amusing Ourselves to Death', 'Nassim Nicholas Taleb', 1996, 'Explores the historical and social background of humanity.', 'Social', 1, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1016SO', 'Thinking in Systems', 'Michio Kaku', 2004, 'Explores the historical and social background of humanity.', 'Social', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1017SO', 'Freakonomics', 'Richard Dawkins', 2007, 'A blend of storytelling and science, making learning enjoyable.', 'Social', 1, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1018SC', 'Your Inner Fish', 'Neil deGrasse Tyson', 2011, 'A revolutionary take on scientific concepts explained clearly.', 'Science', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1019SC', 'A Short History of Nearly Everything', 'Carl Sagan', 2002, 'Covers complex topics in a simple, approachable style.', 'Science', 3, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1020SO', 'Justice', 'Richard Dawkins', 2000, 'Thought-provoking and thoroughly researched narrative.', 'Social', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1021SC', 'The Gene', 'Stephen Jay Gould', 2015, 'An exploration of the genetic roots of humanity.', 'Science', 1, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1022SO', 'Outliers', 'Malcolm Gladwell', 2008, 'Investigates what makes people successful.', 'Social', 4, 4);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1023SO', 'The Tipping Point', 'Malcolm Gladwell', 2000, 'How small actions create big social change.', 'Social', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1024SC', 'Astrophysics for People in a Hurry', 'Neil deGrasse Tyson', 2017, 'Quick lessons on deep space topics.', 'Science', 5, 5);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('1025SO', 'Guns, Germs, and Steel', 'Jared Diamond', 1997, 'The fates of human societies explained.', 'Social', 3, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('001SC', 'The Origin of Species', 'Charles Darwin', 1859, 'Foundational work on evolutionary biology and natural selection.', 'Science', 4, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('002SO', 'Democracy in America', 'Alexis de Tocqueville', 1835, 'Classic analysis of American democratic society and institutions.', 'Social', 3, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('003SC', 'Principia Mathematica', 'Isaac Newton', 1687, 'Mathematical principles of natural philosophy and physics.', 'Science', 2, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('004SO', 'The Wealth of Nations', 'Adam Smith', 1776, 'Foundational text in classical economics and free market theory.', 'Social', 5, 4);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('005SC', 'Silent Spring', 'Rachel Carson', 1962, 'Environmental science book about pesticides and ecological impact.', 'Science', 3, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('006SO', 'The Social Contract', 'Jean-Jacques Rousseau', 1762, 'Political philosophy on legitimate political order and democracy.', 'Social', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('007SC', 'The Double Helix', 'James Watson', 1968, 'Personal account of DNA structure discovery in molecular biology.', 'Science', 4, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('008SO', 'Bowling Alone', 'Robert Putnam', 2000, 'Analysis of declining social capital in American society.', 'Social', 3, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('009SC', 'The Selfish Gene', 'Richard Dawkins', 1976, 'Gene-centered view of evolution and evolutionary biology.', 'Science', 5, 5);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('010SO', 'Capital in the Twenty-First Century', 'Thomas Piketty', 2013, 'Economic analysis of wealth inequality in modern capitalism.', 'Social', 2, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('011SC', 'Relativity', 'Albert Einstein', 1916, 'Introduction to special and general theory of relativity.', 'Science', 3, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('012SO', 'The Protestant Ethic and the Spirit of Capitalism', 'Max Weber', 1905, 'Sociological study on religion and economic development.', 'Social', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('013SC', 'The Structure of Scientific Revolutions', 'Thomas Kuhn', 1962, 'Philosophy of science and paradigm shifts in scientific progress.', 'Science', 4, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('014SO', 'Freakonomics', 'Steven Levitt', 2005, 'Economic analysis of everyday life and social phenomena.', 'Social', 6, 4);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('015SC', 'The Elegant Universe', 'Brian Greene', 1999, 'String theory and modern physics for general audiences.', 'Science', 3, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('016SO', 'The Clash of Civilizations', 'Samuel Huntington', 1996, 'Political theory on cultural conflicts in post-Cold War world.', 'Social', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('017SC', 'Godel Escher Bach', 'Douglas Hofstadter', 1979, 'Exploration of consciousness, mathematics, and artificial intelligence.', 'Science', 4, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('018SO', 'The Theory of Moral Sentiments', 'Adam Smith', 1759, 'Philosophical work on ethics and human moral psychology.', 'Social', 3, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('019SC', 'The Feynman Lectures on Physics', 'Richard Feynman', 1964, 'Comprehensive introduction to undergraduate physics.', 'Science', 5, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('020SO', 'The End of History', 'Francis Fukuyama', 1992, 'Political theory on liberal democracy as final form of government.', 'Social', 2, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('021SC', 'The Fabric of the Cosmos', 'Brian Greene', 2004, 'Space, time, and the texture of reality in modern physics.', 'Science', 3, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('022SO', 'The Power Elite', 'C. Wright Mills', 1956, 'Sociological analysis of power structure in American society.', 'Social', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('023SC', 'The Immortal Life of Henrietta Lacks', 'Rebecca Skloot', 2010, 'Medical ethics and scientific research in cell biology.', 'Science', 4, 4);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('024SO', 'The Presentation of Self in Everyday Life', 'Erving Goffman', 1956, 'Sociological study of social interaction and identity.', 'Social', 3, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('025SC', 'The Emperor of All Maladies', 'Siddhartha Mukherjee', 2010, 'Biography of cancer and history of oncology.', 'Science', 5, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('026SO', 'The Sociological Imagination', 'C. Wright Mills', 1959, 'Connecting personal troubles to public issues in sociology.', 'Social', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('027SC', 'The Quantum Universe', 'Brian Cox', 2011, 'Quantum mechanics and particle physics for general readers.', 'Science', 3, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('028SO', 'The Lonely Crowd', 'David Riesman', 1950, 'Study of American character and social conformity.', 'Social', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('029SC', 'The Gene', 'Siddhartha Mukherjee', 2016, 'History and future of genetics and genomics.', 'Science', 4, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('030SO', 'The Interpretation of Cultures', 'Clifford Geertz', 1973, 'Anthropological approach to understanding culture and society.', 'Social', 3, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('031SC', 'The Code Book', 'Simon Singh', 1999, 'History of cryptography and information security.', 'Science', 3, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('032SO', 'The Affluent Society', 'John Kenneth Galbraith', 1958, 'Economic critique of consumer culture and wealth distribution.', 'Social', 2, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('033SC', 'The Sixth Extinction', 'Elizabeth Kolbert', 2014, 'Environmental science on current mass extinction event.', 'Science', 4, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('034SO', 'The McDonaldization of Society', 'George Ritzer', 1993, 'Sociological analysis of rationalization in modern society.', 'Social', 3, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('035SC', 'The Hidden Dimensions', 'Brian Greene', 2005, 'Extra dimensions and string theory in modern physics.', 'Science', 2, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('036SO', 'The Managed Heart', 'Arlie Hochschild', 1983, 'Emotional labor and commercialization of human feeling.', 'Social', 3, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('037SC', 'The Particle at the End of the Universe', 'Sean Carroll', 2012, 'Discovery of Higgs boson and particle physics.', 'Science', 4, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('038SO', 'The Other America', 'Michael Harrington', 1962, 'Poverty and social inequality in affluent America.', 'Social', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('039SC', 'The Botany of Desire', 'Michael Pollan', 2001, 'Plant-human relationships and evolutionary biology.', 'Science', 3, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('040SO', 'The Feminine Mystique', 'Betty Friedan', 1963, 'Feminist analysis of women role in 1950s American society.', 'Social', 4, 4);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('041SC', 'The Ancestor Tale', 'Richard Dawkins', 2004, 'Evolutionary biology and common ancestry of life.', 'Science', 3, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('042SO', 'The Culture of Narcissism', 'Christopher Lasch', 1979, 'Psychological and social critique of American individualism.', 'Social', 2, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('043SC', 'The Information', 'James Gleick', 2011, 'History and theory of information and communication.', 'Science', 4, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('044SO', 'The Authoritarian Personality', 'Theodor Adorno', 1950, 'Psychological study of fascism and authoritarian tendencies.', 'Social', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('045SC', 'The Omnivore Dilemma', 'Michael Pollan', 2006, 'Food systems, agriculture, and environmental impact.', 'Science', 5, 4);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('046SO', 'The Power of Myth', 'Joseph Campbell', 1988, 'Comparative mythology and human cultural patterns.', 'Social', 3, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('047SC', 'The Age of Wonder', 'Richard Holmes', 2008, 'Scientific discovery in the Romantic Age of science.', 'Science', 3, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('048SO', 'The Pursuit of Loneliness', 'Philip Slater', 1970, 'American culture and social isolation in modern society.', 'Social', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('049SC', 'The Righteous Mind', 'Jonathan Haidt', 2012, 'Moral psychology and evolutionary basis of ethics.', 'Science', 4, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('050SO', 'The Fall of Public Man', 'Richard Sennett', 1977, 'Public life and private identity in modern society.', 'Social', 2, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('051SC', 'The Vital Question', 'Nick Lane', 2015, 'Energy, evolution, and the origins of complex life.', 'Science', 3, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('052SO', 'The Civilizing Process', 'Norbert Elias', 1939, 'Historical sociology of manners and social control.', 'Social', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('053SC', 'The Moral Landscape', 'Sam Harris', 2010, 'Science and human values in moral philosophy.', 'Science', 3, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('054SO', 'The Spirit Level', 'Richard Wilkinson', 2009, 'Inequality and social problems in developed societies.', 'Social', 4, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('055SC', 'The Demon-Haunted World', 'Carl Sagan', 1995, 'Science as candle in the dark against pseudoscience.', 'Science', 5, 4);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('056SO', 'The Great Transformation', 'Karl Polanyi', 1944, 'Political and economic origins of our time.', 'Social', 2, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('057SC', 'The Making of the Atomic Bomb', 'Richard Rhodes', 1986, 'History of nuclear physics and Manhattan Project.', 'Science', 3, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('058SO', 'The Naked Ape', 'Desmond Morris', 1967, 'Human behavior from zoological perspective.', 'Social', 3, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('059SC', 'The Man Who Loved Only Numbers', 'Paul Hoffman', 1998, 'Biography of mathematician Paul Erdos.', 'Science', 2, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('060SO', 'The Tipping Point', 'Malcolm Gladwell', 2000, 'How little things make big difference in social change.', 'Social', 6, 5);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('061SC', 'The First Three Minutes', 'Steven Weinberg', 1977, 'Modern view of origin of the universe.', 'Science', 3, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('062SO', 'The Outsiders', 'Howard Becker', 1963, 'Sociological studies in deviance and social control.', 'Social', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('063SC', 'The Elegant Solution', 'Matthew May', 2006, 'Problem-solving through simplicity and innovation.', 'Science', 4, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('064SO', 'The Organization Man', 'William Whyte', 1956, 'Corporate culture and conformity in American business.', 'Social', 2, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('065SC', 'The Whole Earth Catalog', 'Stewart Brand', 1968, 'Tools and ideas for sustainable living and ecology.', 'Science', 3, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('066SO', 'The Presentation of Self', 'Erving Goffman', 1959, 'Social psychology and impression management.', 'Social', 3, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('067SC', 'The Quantum Theory Cannot Hurt You', 'Marcus Chown', 2007, 'Accessible introduction to quantum mechanics.', 'Science', 2, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('068SO', 'The Hidden Persuaders', 'Vance Packard', 1957, 'Psychological manipulation in advertising and marketing.', 'Social', 4, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('069SC', 'The Periodic Table', 'Primo Levi', 1975, 'Chemistry and life through elements and compounds.', 'Science', 3, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('070SO', 'The Status Seekers', 'Vance Packard', 1959, 'Social stratification and class consciousness in America.', 'Social', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('071SC', 'The Innovators Dilemma', 'Clayton Christensen', 1997, 'Technology and innovation in business and science.', 'Science', 5, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('072SO', 'The Wisdom of Crowds', 'James Surowiecki', 2004, 'Collective intelligence and group decision-making.', 'Social', 4, 4);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('073SC', 'The Stuff of Thought', 'Steven Pinker', 2007, 'Language, cognition, and human nature.', 'Science', 3, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('074SO', 'The True Believer', 'Eric Hoffer', 1951, 'Psychology of mass movements and fanaticism.', 'Social', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('075SC', 'The Radioactive Boy Scout', 'Ken Silverstein', 2004, 'Nuclear science and teenage scientific experimentation.', 'Science', 3, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('076SO', 'The Maze of Influence', 'Richard Pells', 1985, 'Intellectuals and political power in Cold War America.', 'Social', 2, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('077SC', 'The Disappearing Spoon', 'Sam Kean', 2010, 'Periodic table and history of chemical elements.', 'Science', 4, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('078SO', 'The Lonely American', 'Jacqueline Olds', 2009, 'Social isolation and community in modern society.', 'Social', 3, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('079SC', 'The Quantum Dot', 'Richard Turton', 1995, 'Nanotechnology and quantum physics applications.', 'Science', 2, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('080SO', 'The Presentation of Everyday Life', 'Peter Berger', 1961, 'Social construction of reality and meaning.', 'Social', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('081SC', 'The Dancing Wu Li Masters', 'Gary Zukav', 1979, 'Physics and Eastern philosophy for general readers.', 'Science', 3, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('082SO', 'The Pursuit of Happiness', 'David Myers', 1992, 'Psychology of well-being and life satisfaction.', 'Social', 4, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('083SC', 'The Ancestor of the Future', 'Freeman Dyson', 1988, 'Technology, science, and human future.', 'Science', 2, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('084SO', 'The Lonely Crowd Revisited', 'David Riesman', 1977, 'Updated analysis of American social character.', 'Social', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('085SC', 'The Trouble with Physics', 'Lee Smolin', 2006, 'Critique of string theory and modern physics.', 'Science', 3, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('086SO', 'The Social Animal', 'David Brooks', 2011, 'Unconscious mind and social behavior.', 'Social', 5, 4);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('087SC', 'The Blank Slate', 'Steven Pinker', 2002, 'Human nature and the tabula rasa concept.', 'Science', 4, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('088SO', 'The Bowling Alone Revisited', 'Robert Putnam', 2020, 'Updated analysis of American social capital.', 'Social', 3, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('089SC', 'The God Particle', 'Leon Lederman', 1993, 'Search for fundamental particles in physics.', 'Science', 3, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('090SO', 'The Nurture Assumption', 'Judith Harris', 1998, 'Child development and peer influence vs parenting.', 'Social', 2, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('091SC', 'The Singularity is Near', 'Ray Kurzweil', 2005, 'Technology and artificial intelligence future.', 'Science', 4, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('092SO', 'The Pursuit of the Millennium', 'Norman Cohn', 1957, 'Social movements and revolutionary millenarianism.', 'Social', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('093SC', 'The Wonders of Physics', 'Michio Kaku', 1994, 'Introduction to modern physics and cosmology.', 'Science', 3, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('094SO', 'The Transparent Society', 'David Brin', 1998, 'Privacy, surveillance, and technology in society.', 'Social', 3, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('095SC', 'The Elegant Hypothesis', 'Brian Greene', 2003, 'Mathematical beauty and physical theories.', 'Science', 2, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('096SO', 'The Decline of the West', 'Oswald Spengler', 1918, 'Philosophy of history and cultural cycles.', 'Social', 2, 1);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('097SC', 'The Ascent of Science', 'Brian Silver', 1998, 'History and philosophy of scientific progress.', 'Science', 3, 2);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('098SO', 'The Power of Now', 'Eckhart Tolle', 1997, 'Spiritual guide to enlightenment and consciousness.', 'Social', 6, 5);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('099SC', 'The Theory of Everything', 'Stephen Hawking', 2002, 'Unified field theory and fundamental physics.', 'Science', 4, 3);");
                stmt.execute("INSERT INTO books (id, title, author, publicationYear, description, category, total_stock, available_stock) VALUES ('100SO', 'The Age of Extremes', 'Eric Hobsbawm', 1994, 'History of the short twentieth century.', 'Social', 3, 2);");
            }
        }
    }
    
    @Override
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) { 
            System.err.println("Gagal menutup koneksi database.");
            e.printStackTrace(); 
        }
    }
}