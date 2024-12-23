import java.sql.*;
import java.util.*;
import java.util.Date;

// Interface for CRUD operations
interface Manageable {
    void create(); // Method untuk menambah data
    void read(); // Method untuk membaca data
    void update(); // Method untuk mengubah data
    void delete(); // Method untuk menghapus data
}

// Superclass Item
class Item {
    protected int id; //variabel untuk menambah ID unik item
    protected String name; //variabel untuk menambah nama item
    protected String description; //variabel untuk menambah deskripsi item
    protected Date dateReported; //variabel untuk menambah tanggal item dilaporkan
    //constructor
    public Item(int id, String name, String description, Date dateReported) {
        this.id = id; //constructor untuk mengisi ID
        this.name = name; //constructor untuk mengisi nama item
        this.description = description; //constructor untuk mengisi deskripsi item
        this.dateReported = dateReported; //constructor untuk mengisi tanggal item dilaporkan
    }
    //getter methods untuk mengakses properti
    public int getId() { return id; } //getter untuk mengambil nilai ID
    public String getName() { return name; } //getter untuk mengambil nilai nama item
    public String getDescription() { return description; } //getter untuk mengambil nilai deskripsi item
    public Date getDateReported() { return dateReported; } //getter untuk mengambil nilai tanggal item dilaporkan
}

// Subclass untuk barang hilang
class LostItem extends Item {
    private String lastSeenLocation; //variabel untuk menambahkan lokasi terakhir lihat
    private String reporterName; //variabel untuk menmbahkan nama pelapor
    //constructor
    public LostItem(int id, String name, String description, Date dateReported, String lastSeenLocation, String reporterName) {
        super(id, name, description, dateReported); //memanggil constructor superclass
        this.lastSeenLocation = lastSeenLocation; //constructor untuk mengisi lokasi terakhir lihat
        this.reporterName = reporterName; //constructor untuk mengisi nama pelapor
    }
    //getter methods untuk mengakses properti 
    public String getLastSeenLocation() { return lastSeenLocation; }
    public String getReporterName() { return reporterName; }
}

// Subclass untuk barang ditemukan
class FoundItem extends Item {
    private String foundLocation;
    private String finderName;

    public FoundItem(int id, String name, String description, Date dateReported, String foundLocation, String finderName) {
        super(id, name, description, dateReported); //memanggil constructor superclass
        this.foundLocation = foundLocation; //constructor untuk mengisi lokasi barang ditemukan
        this.finderName = finderName; //constructor untuk mengisi nama penemu
    }
    //getter methods untuk mengakses properti
    public String getFoundLocation() { return foundLocation; }
    public String getFinderName() { return finderName; }
}

// class untuk manajemen barang implementing CRUD operations
class ItemManager implements Manageable {
    private Connection connection;
    //konstruktor untuk menghubungkan ke database
    public ItemManager() {
        try { //exception handling
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/lostAndFound", "postgres", "dec15may");
            System.out.println("\nKoneksi ke database berhasil.");
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
            connection = null;
        }
    }

    @Override
    public void create() {
        //menambahkan data barang ke database
        Scanner scanner = new Scanner(System.in);
        System.out.print("Masukkan jenis item (1: Hilang, 2: Temuan): ");
        int type = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try { //exception handling
            System.out.print("Masukkan nama barang: ");
            String name = scanner.nextLine().toUpperCase(); //menjadikan semua huruf kapital
            System.out.print("Masukkan deskripsi barang: ");
            String description = scanner.nextLine();
            
            Timestamp dateReported = new Timestamp(new Date().getTime()); //method date 

            //percabangan if-else untuk menentukan pengguna ingin memasukkan barang hilang atau barang temuan
            if (type == 1) {
                // input untuk barang hilang
                System.out.print("Masukkan lokasi terakhir terlihat: ");
                String lastSeenLocation = scanner.nextLine();
                System.out.print("Masukkan nama pelapor: ");
                String reporterName = scanner.nextLine().trim(); //menghapus spasi diawal dan akhir

                String query = "INSERT INTO lost_items (name, description, date_reported, last_seen_location, reporter_name) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setString(2, description);
                stmt.setTimestamp(3, dateReported);
                stmt.setString(4, lastSeenLocation);
                stmt.setString(5, reporterName);
                stmt.executeUpdate();


            } else if (type == 2) {
                //input unutuk barang temuan
                System.out.print("Masukkan lokasi temuan: ");
                String foundLocation = scanner.nextLine();
                System.out.print("Masukkan nama penemu: ");
                String finderName = scanner.nextLine().trim(); //menghapus spasi diawal dan akhir

                
                String query = "INSERT INTO found_items (name, description, date_reported, found_location, finder_name) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setString(2, description);
                stmt.setTimestamp(3, dateReported);
                stmt.setString(4, foundLocation);
                stmt.setString(5, finderName);
                stmt.executeUpdate();
                }

                
            

            System.out.println("\nItem berhasil ditambahkan!");
        } catch (SQLException e) {
            System.out.println("Error inserting data: " + e.getMessage());
        }
        
    }

    @Override
    public void read() {
        //membaca barang dari database
        try { //exception handling
            int totalLostItems = 0;
            int totalFoundItems = 0;
    
            System.out.println("\n=== Daftar Barang Hilang ===");
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM lost_items");
            //perulangan untuk membaca data dari database dan mencetaknya
            while (rs.next()) {
                totalLostItems++; //penghitungan matematika untuk jumlah barang hilang
                System.out.println("ID: " + rs.getInt("id") + ", Nama: " + rs.getString("name") + ", Lokasi Terakhir: " + rs.getString("last_seen_location"));
            }
            System.out.println("Total Barang Hilang: " + totalLostItems);
    
            System.out.println("\n=== Daftar Barang Temuan ===");
            rs = stmt.executeQuery("SELECT * FROM found_items");
            while (rs.next()) {
                totalFoundItems++; //penghitungan matematika untuk jumlah barang temuan
                System.out.println("ID: " + rs.getInt("id") + ", Nama: " + rs.getString("name") + ", Lokasi Temuan: " + rs.getString("found_location"));
            }
            System.out.println("Total Barang Ditemukan: " + totalFoundItems);
    
        } catch (SQLException e) {
            System.out.println("Error reading data: " + e.getMessage());
        }
    }    

    @Override
    public void update() {
        //mengupdate data barang di database
        Scanner scanner = new Scanner(System.in);
        System.out.print("Masukkan jenis item (1: Hilang, 2: Temuan): ");
        int type = scanner.nextInt();
        System.out.print("Masukkan ID item yang ingin diupdate: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Masukkan nama baru: ");
        String name = scanner.nextLine().toUpperCase(); //menkadilan semua huruf kapital
        System.out.print("Masukkan deskripsi baru: ");
        String description = scanner.nextLine();

        try { //exception handling
            //percabangan if-else untuk menentukan pengguna ingin mengupdate barang hilang atau barang temuan
            if (type == 1) { 
                System.out.print("Masukkan lokasi baru: ");
                String lastSeenLocation = scanner.nextLine();
                System.out.print("Masukkan nama pelapor baru: ");
                String reporterName = scanner.nextLine().trim();

                String query = "UPDATE lost_items SET name = ?, description = ?, last_seen_location = ?, reporter_name = ? WHERE id = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setString(2, description);
                stmt.setString(3, lastSeenLocation);
                stmt.setString(4, reporterName);
                stmt.setInt(5, id);
                int rowsUpdated = stmt.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("\nData berhasil diupdate!");
                } else {
                    System.out.println("\nID tidak ditemukan.");
                }


            } else if (type == 2) {
                System.out.print("Masukkan lokasi baru: ");
                String foundLocation = scanner.nextLine();
                System.out.print("Masukkan nama baru: ");
                String finderName = scanner.nextLine().trim();

                
                String query = "UPDATE found_items SET name = ?, description = ?, found_location = ?, finder_name = ? WHERE id = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setString(2, description);
                stmt.setString(3, foundLocation);
                stmt.setString(4, finderName);
                stmt.setInt(5, id);
                int rowsUpdated = stmt.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("\nData berhasil diupdate!");
                } else {
                    System.out.println("\nID tidak ditemukan.");
                }

            }

            
            
        } catch (SQLException e) {
            System.out.println("Error updating data: " + e.getMessage());
        }
        
    }

    @Override
    public void delete() {
        //menghapus data barang di database
        Scanner scanner = new Scanner(System.in);
        System.out.print("Masukkan jenis item (1: Hilang, 2: Temuan): ");
        int type = scanner.nextInt();
        System.out.print("Masukkan ID item yang ingin dihapus: ");
        int id = scanner.nextInt();

        try { //exception handling
            //percabangan if-else untuk menentukan pengguna ingin menghapus barang hilang atau barang temuan
            if (type == 1) {
            String query = "DELETE FROM lost_items WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("\nData berhasil dihapus!");
            } else {
                System.out.println("\nID tidak ditemukan.");
            }
            }
            if (type == 2) {
                String query = "DELETE FROM found_items WHERE id = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setInt(1, id);
                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("\nData berhasil dihapus!");
                } else {
                    System.out.println("\nID tidak ditemukan.");
                }
                }
        } catch (SQLException e) {
            System.out.println("Error deleting data: " + e.getMessage());
        }
        
    }
}

// Main Class
public class LostAndFoundSystem {
    public static void main(String[] args) {
        //program untuk menjalankan sistem pengelolaan barang hilang
        ItemManager manager = new ItemManager();
        Scanner scanner = new Scanner(System.in);
        try{ //exception handling
            //perulangan tak hingga (infinite loop) untuk menampilkan menu utama
            while (true) {
                //menampilkan menu utama
                System.out.println("\n=== Sistem Pengelolaan Barang Hilang ===");
                System.out.println("1. Tambah Item");
                System.out.println("2. Lihat Item");
                System.out.println("3. Update Item");
                System.out.println("4. Hapus Item");
                System.out.println("5. Keluar");
                System.out.print("Pilih menu: ");

                int choice = scanner.nextInt();
                //percabangan switch-case untuk mengesekusi aksi berdasarkan pilihan pengguna
                switch (choice) {
                    case 1:
                        manager.create(); //menambah data
                        break;
                    case 2:
                        manager.read(); //melihat/membaca data
                        break;
                    case 3:
                        manager.update(); // mengupdate data
                        break;
                    case 4:
                        manager.delete(); //menghapus data
                        break;
                    case 5:
                        System.out.println("\nTerima kasih telah menggunakan sistem ini!");
                        return; //keluar dari program
                    default:
                        System.out.println("\nPilihan tidak valid.");
                }
                
            
            }
            
        } finally {
            scanner.close(); 
            }
    }
}
