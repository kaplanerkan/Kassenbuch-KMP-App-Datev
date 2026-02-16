# Kassenbuch - Küçük İşletmeler için KMP Uygulaması

> Dönerci, pizzacı ve küçük gastronomi işletmeleri için dijital kasa defteri.
> Almanya GoBD kurallarına uygun · DATEV dışa aktarımı · Material 3 · Offline · Android, Desktop & iOS

---

## Bu nedir?

Almanya'daki GoBD kurallarına uygun dijital kasa defteri uygulaması. Kotlin Multiplatform (KMP) ve Compose Multiplatform ile yazılmış olup Android, Windows, macOS, Linux ve iOS üzerinde çalışır. Özellikle dönerci, pizzacı gibi küçük gastronomi işletmeleri için geliştirilmiştir.

---

## Özellikler

| Özellik | Açıklama |
|---------|----------|
| **Dashboard** | Kasa bakiyesi, günlük/aylık gelir-gider özeti |
| **Buchung (Kayıt)** | Tarih, saat, tutar, kayıt türü, karşı hesap ile gelir-gider kaydı |
| **Beleg-Foto (Fiş Fotoğrafı)** | Fişleri kamerayla çekip kayda ekleme |
| **Münzzähler (Para Sayıcı)** | Kasadaki parayı madeni para ve banknot olarak sayma |
| **Tagesabschluss (Gün Sonu)** | Devir, gelir, gider, kasa farkı hesaplama |
| **DATEV Export** | Muhasebeciye gönderilebilir DATEV CSV formatı |
| **CSV Export** | Excel/Sheets için basit tablo |
| **Einstellungen (Ayarlar)** | İşletme bilgileri, DATEV numaraları, para üstü ayarı |
| **Karanlık/Aydınlık Tema** | Otomatik karanlık mod desteği |
| **Duyarlı Tasarım** | Telefonda alt menü, tablette/masaüstünde yan menü |

---

## Günlük Kullanım Akışı

```
Sabah:  Wechselgeld Einlage (100 €) → Buchung ekle
Gün:    Satış yap, gerekirse Netto'dan alışveriş kaydet
Akşam:  Z-Bericht kes → BAR satışı kaydet (7 %/19 % ayrı)
        → Wechselgeld Entnahme → Tagesabschluss
Haftalık: Bankaya nakit yatır → Bankeinzahlung kaydet
Ay Sonu: DATEV Export → Muhasebecine gönder
```

---

## Notlar

- **Bu bir vergi danışmanlığı değildir.** Tüm ayarları ve dışa aktarımları muhasebecine danışarak kullanın.
- DATEV Export'taki Berater-Nr. ve Mandanten-Nr. bilgilerini mutlaka muhasebecinden alın.
- Z-Berichte ve tüm belgeler 10 yıl saklanmalıdır (§ 147 AO).
- Uygulama tamamen offline çalışır — internet gerektirmez.

---

## Lisans

Bu proje [MIT Lisansı](LICENSE) ile lisanslanmıştır.
