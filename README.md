# Kasir Warmindo
#### Aplikasi ini diperuntukkan bagi warmindo yang sudah memiliki cabang, untuk memudahkan dalam melayani pelanggan yang memesan makanan dan minuman, kasir bisa dengan mudah melakukan order, menambah menu, edit menu, menghapus menu, dan mencari menu berdasarkan kategori dan nama menu. Kasir juga bisa melihat history dari order pada akun itu, jadi setiap cabang memiliki akun yang berbeda dan data history order yang berbeda

# Use Case
![image](https://github.com/Ayash13/CRUD_JetpackCompose/assets/93539509/e2eda982-6041-4b25-afd4-b5d42cd289c3)

- Login ( Kasir dapat melakukan login menggunakan email dan password yang sudah dibuatkan oleh admin langsung dari firebase auth )
- Display Menu ( Tampilan dari menu warmindo yang datanya diambil dari firestore )
- Search by Name & Category ( Fitur search untuk memudahkan dalam mencari menu berdasarkan nama menu dan kategori )
- Add Menu ( Menambahkan menu berupa gambar menu, nama menu, kategori menu, dan harga menu lalu menyimpannya ke firebase firestore dan firebase storage untuk gambar menu )
- Delete Menu ( Menghapus menu berdasarkan menu yang dipilih )
- Edit Menu ( Mengubah menu berdasarkan menu yang dipilih )
- Cart ( Ketika klik order pada menu, maka akan masuk ke cart terlebih dahulu untuk memungkinkan membeli item lebih dari satu )
- Order ( Melalukan order pada item yang berada di dalam cart berarti menyimpan semuanya ke dalam firebase firestore yang id documentnya berdasarkan akun saat ini, itu juga berarti berhasil order lalu mengkosongkan cart )
- History ( Menampilkan riwayat order berdasarkan akun saat ini )
- Logout ( Melakukan logout dari firebase auth dan redirect ke halaman login )
