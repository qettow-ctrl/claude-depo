# claude-depo
Claude için açıyorum bu depoyu

## Hatırla — kilit ekranında duran notlar

Basit bir Android not uygulaması. Yazdığın her not, kilit ekranında ve bildirim
panelinde kalıcı bir bildirim olarak durur; telefona bakınca ne yapman gerektiğini
hatırlarsın.

### Kurulum
1. Depodaki **Releases → "Hatırla (son sürüm)"** sayfasını telefondan aç.
2. `Hatirla.apk` dosyasını indir ve aç (gerekirse "bilinmeyen kaynaklardan
   yüklemeye izin ver"i aç).
3. Uygulama ilk açılışta bildirim izni ister: **İzin ver**.

### Özellikler
- **+ düğmesi:** uygulamada sağ alttaki büyük + ile not ekle.
- **Uygulama ikonu kısayolu:** ikona basılı tut → "Yeni not".
- **Ana ekran widget'ı:** notların listesi ve + düğmesi (ana ekrana basılı tut →
  Widget'lar → Hatırla).
- **Hızlı ayar kutucuğu:** bildirim panelini aşağı çek → düzenle → "+ Not"
  kutucuğunu ekle.
- **Kilit ekranı:** her not ayrı bir bildirimdir. Bildirimdeki **Tamam** notu
  siler, bildirime dokunmak notu düzenler.
- Telefon yeniden başladığında bildirimler kendiliğinden geri gelir.

> Kilit ekranında görünmüyorsa: Ayarlar → Bildirimler → Kilit ekranı →
> "Tüm bildirim içeriğini göster" seçili olmalı.

### Geliştirme
`./gradlew assembleDebug` — her push'ta GitHub Actions APK'yı derleyip
"son-surum" sürümüne yükler.
