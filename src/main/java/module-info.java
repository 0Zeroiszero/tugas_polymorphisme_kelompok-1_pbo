module id.kelompok1.pbo.polymorp_obj {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    // Wajib: agar FXML bisa mengakses controller via reflection
    opens id.kelompok1.pbo.polymorp_obj to javafx.fxml;
    opens id.kelompok1.pbo.polymorp_obj.ui to javafx.fxml;

    // Ekspor package utama
    exports id.kelompok1.pbo.polymorp_obj;
}