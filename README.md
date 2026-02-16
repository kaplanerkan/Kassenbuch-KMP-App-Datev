# Kassenbuch - KMP App für kleine Betriebe

> Digitales Kassenbuch als Kotlin Multiplatform App für Döner-Imbisse, Restaurants und kleine Gastronomiebetriebe.
> GoBD-konform · DATEV-Export · Material 3 Design · Offline-fähig · Android, Desktop & iOS

---

## Was ist das?

Eine plattformübergreifende App zum Führen eines digitalen Kassenbuchs nach deutschen Buchhaltungsvorschriften (GoBD). Die gesamte Geschäftslogik und UI ist in Kotlin Multiplatform (KMP) mit Compose Multiplatform geschrieben und läuft auf Android, Windows, macOS, Linux und iOS.

---

## Screenshots

| Desktop (Windows) | Android |
|:--:|:--:|
| ![Desktop Dashboard](screenshots/AnyDesk_LUu2tIrOCU.png) | ![Android Dashboard](screenshots/AnyDesk_Vgp4XwIt7v.png) |

---

## Funktionen

| Feature | Beschreibung |
|---------|-------------|
| **Dashboard** | Kassenbestand, Tageseinnahmen/-ausgaben, Monatsübersicht auf einen Blick |
| **Buchungen** | Einnahmen & Ausgaben mit Datum, Uhrzeit, Betrag, Buchungsart und Gegenkonto |
| **Belegfotos** | Belege direkt fotografieren und zur Buchung speichern |
| **Münzzähler** | Schein- und Münzzähler für den Kassensturz |
| **Tagesabschluss** | Vortrag, Einnahmen, Ausgaben, Endbestand und Kassendifferenz |
| **DATEV-Export** | Buchungsstapel im DATEV-Format (EXTF v12.0) für den Steuerberater |
| **CSV-Export** | Kassenbuch als CSV für Excel / Google Sheets |
| **Einstellungen** | Betriebsdaten, DATEV Berater-/Mandanten-Nr., Wechselgeld-Standard |
| **Dark/Light Theme** | Material 3 mit automatischem Dark Mode |
| **Responsive Layout** | NavigationBar (Telefon) / NavigationRail (Tablet/Desktop) |

---

## Benutzerhandbuch

### Übersicht (Dashboard)

Nach dem Start der App wird das **Dashboard** angezeigt. Hier sieht der Benutzer auf einen Blick:

- **Kassenbestand**: Der aktuelle Bargeldbestand in der Kasse, berechnet aus allen bisherigen Buchungen.
- **Tageseinnahmen / Tagesausgaben**: Die Summe aller Einnahmen und Ausgaben des heutigen Tages.
- **Monatseinnahmen / Monatsausgaben**: Die Summe aller Einnahmen und Ausgaben des laufenden Monats.
- **Letzte Buchungen**: Eine Schnellansicht der zuletzt erfassten Buchungen.

Über die **Schnellaktionen** kann der Benutzer direkt eine neue Buchung anlegen, zum Münzzähler wechseln oder alle Buchungen anzeigen lassen.

**Worauf ist zu achten?**
- Der angezeigte Kassenbestand muss jederzeit mit dem tatsächlichen Bargeld in der Kasse übereinstimmen. Bei Abweichungen sofort prüfen, ob eine Buchung fehlt.
- Das Dashboard zeigt nur eine Zusammenfassung. Für eine vollständige Tagesübersicht den Tab **Tagesabschluss** verwenden.

---

### Buchungen

Im Tab **Buchungen** werden alle Einnahmen und Ausgaben des gewählten Tages aufgelistet. Über das Kalender-Symbol in der oberen Leiste kann ein anderer Tag ausgewählt werden.

**Neue Buchung anlegen:**
1. Auf den **+**-Button (unten rechts) tippen.
2. **Datum und Uhrzeit** werden automatisch auf den aktuellen Zeitpunkt gesetzt, können aber manuell geändert werden.
3. Zwischen **Einnahme** und **Ausgabe** wählen. Die verfügbaren Buchungsarten passen sich automatisch an.
4. Die passende **Buchungsart** auswählen (z. B. „Tagesumsatz 7 %", „Wareneinkauf 19 %"). Gegenkonto, BU-Schlüssel und Steuersatz werden automatisch gemäß SKR03 vorbelegt.
5. Den **Betrag** in Euro eingeben (Bruttobetrag inklusive Mehrwertsteuer).
6. Einen **Buchungstext** eingeben (z. B. „Z-Bericht Speisen", „Netto Einkauf").
7. Optional: **Beleg-Nr.** wird automatisch fortlaufend vergeben und kann angepasst werden.
8. Optional: Über **Belegfoto** einen Kassenbon oder eine Quittung direkt mit der Kamera fotografieren.
9. Optional: Eine **Notiz** zur Buchung hinzufügen.
10. Mit **Speichern** die Buchung abschließen.

**Bestehende Buchung bearbeiten:**
- In der Buchungsliste auf die gewünschte Buchung tippen. Das Formular öffnet sich mit den gespeicherten Werten und kann geändert werden.

**Worauf ist zu achten?**
- Der Betrag ist immer der **Bruttobetrag** (inklusive Mehrwertsteuer). Die Netto-/Steueraufteilung erfolgt automatisch beim DATEV-Export.
- Jeder Vorgang muss einzeln und zeitnah erfasst werden (GoBD-Pflicht).
- Bei Barverkäufen: Den Z-Bericht der Registrierkasse als Grundlage verwenden. Den **Gesamt-Bar-Umsatz** aus dem Z-Bericht als Buchung erfassen (getrennt nach 7 % und 19 %).
- Belege (Kassenbons, Quittungen, Z-Berichte) immer aufbewahren — entweder als Foto in der App oder in Papierform im Ordner.

---

### Tagesabschluss

Im Tab **Tagesabschluss** wird die tägliche Kassenabrechnung durchgeführt. Der Benutzer sieht:

- **Vortrag**: Der Kassenbestand vom Vortag (Übertrag).
- **Einnahmen**: Alle Einnahmen des Tages.
- **Ausgaben**: Alle Ausgaben des Tages.
- **Soll-Endbestand**: Der rechnerische Kassenbestand (Vortrag + Einnahmen − Ausgaben).

**Kassensturz durchführen:**
1. Das Bargeld in der Kasse zählen. Dafür kann der integrierte **Münzzähler** verwendet werden (erreichbar über das Dashboard oder die Navigation).
2. Den **gezählten Betrag** in das Feld „Gezählter Bestand" eingeben.
3. Die App zeigt automatisch die **Differenz** zwischen Soll-Bestand und gezähltem Bestand an.
4. Mit **Tagesabschluss durchführen** den Tag abschließen.

**Worauf ist zu achten?**
- Der Tagesabschluss sollte **jeden Abend** nach Geschäftsschluss durchgeführt werden.
- Eine Differenz von wenigen Cent ist bei Barzahlungsgeschäften normal (Rundungsdifferenzen).
- Größere Differenzen (ab ca. 5 €) deuten auf fehlende oder falsche Buchungen hin und sollten sofort geklärt werden.
- Ein abgeschlossener Tag wird mit einem grünen Häkchen markiert und kann nicht erneut abgeschlossen werden.
- Das Kalender-Symbol in der oberen Leiste ermöglicht den Wechsel zu einem anderen Tag, um vergangene Abschlüsse einzusehen.

---

### Export

Im Tab **Export** können die Buchungen für den Steuerberater exportiert werden. Es stehen zwei Formate zur Verfügung:

**DATEV-Export (EXTF v12.0):**
- Erzeugt eine CSV-Datei im offiziellen DATEV-Buchungsstapelformat, die vom Steuerberater direkt in DATEV importiert werden kann.
- Enthält: Umsatz, Soll/Haben, Konto, Gegenkonto, BU-Schlüssel, Belegdatum, Buchungstext und Belegfeld.
- Format entspricht der DATEV-Schnittstelle EXTF Version 12.0.

**Einfacher CSV-Export:**
- Erzeugt eine übersichtliche CSV-Tabelle mit Datum, Uhrzeit, Buchungsart, Betrag, Buchungstext und Beleg-Nr.
- Kann in Excel, Google Sheets oder einem beliebigen Tabellenkalkulationsprogramm geöffnet werden.

**So wird exportiert:**
1. Den gewünschten **Zeitraum** (Von / Bis) über die Datumsfelder festlegen.
2. Auf **DATEV exportieren** oder **CSV exportieren** tippen.
3. Nach erfolgreichem Export erscheint eine Bestätigung mit dem Dateinamen.
4. Über den **Teilen-Button** kann die Datei direkt per E-Mail, Messenger oder Cloud-Speicher an den Steuerberater gesendet werden.

**Worauf ist zu achten?**
- Vor dem ersten DATEV-Export müssen in den **Einstellungen** die Berater-Nr. und Mandanten-Nr. eingetragen werden. Diese Nummern erhält man vom Steuerberater.
- Der Export sollte in der Regel **monatlich** zum Monatsende erfolgen.
- Die exportierte Datei enthält alle Buchungen des gewählten Zeitraums — keine Buchung wird ausgelassen.
- Es empfiehlt sich, die exportierte Datei zusätzlich als Sicherungskopie abzuspeichern.

---

## Technische Architektur

### Plattformen

| Plattform | Status |
|-----------|--------|
| Android | Unterstützt (Min SDK 30) |
| Windows / macOS / Linux | Unterstützt (JVM Desktop) |
| iOS (iPad) | Vorbereitet (Compile Target) |

### Technologie-Stack

| Schicht | Technologie |
|---------|------------|
| **Sprache** | Kotlin 2.2 · Kotlin Multiplatform |
| **UI** | Compose Multiplatform · Material 3 |
| **Navigation** | Compose Navigation (KMP) · Type-Safe Routes |
| **Datenbank** | SQLDelight (Multiplatform SQLite) |
| **DI** | Koin (Multiplatform) |
| **Async** | Kotlin Coroutines + Flow |
| **ViewModel** | Jetpack Lifecycle ViewModel (KMP) |
| **Kamera** | CameraX (Android) |
| **Logging** | Kermit (Multiplatform) |
| **Bilder** | Coil 3 (Multiplatform) |
| **Einstellungen** | Multiplatform Settings |
| **Serialisierung** | kotlinx.serialization |

### Projektstruktur

```
composeApp/src/
├── commonMain/kotlin/de/panda/kassenbuch/
│   ├── App.kt                        # NavHost + HomeContent (Tabs)
│   ├── navigation/Routes.kt          # @Serializable Route-Definitionen
│   ├── data/
│   │   ├── entity/                    # Buchung, Tagesabschluss, BuchungsArt
│   │   ├── db/                        # SQLDelight generierte DB
│   │   └── repository/                # KassenbuchRepository
│   ├── di/CommonModule.kt             # Gemeinsames Koin-Modul
│   ├── ui/
│   │   ├── theme/                     # Material 3 Theme (Dark/Light)
│   │   ├── components/                # AdaptiveLayout, WindowSizeClass
│   │   └── screens/
│   │       ├── dashboard/             # Dashboard + ScreenModel
│   │       ├── booking/               # Buchungsformular + Liste
│   │       ├── daily/                 # Tagesabschluss + Münzzähler
│   │       ├── export/                # DATEV + CSV Export
│   │       ├── settings/              # Einstellungen
│   │       └── info/                  # Plattformspezifische Info
│   ├── platform/                      # expect-Deklarationen
│   └── util/                          # Formatter, DatevExporter, PrefsManager
├── androidMain/                       # Android-spezifisch (Activity, Kamera, SQLite-Treiber)
├── desktopMain/                       # Desktop-spezifisch (JVM Window, SQLite-Treiber)
└── iosMain/                           # iOS-spezifisch (UIViewController, SQLite-Treiber)
```

### SKR03-Konten (integriert)

| Konto | Bezeichnung | Verwendung |
|-------|------------|-----------|
| 1000 | Kasse | Hauptkonto |
| 1200 | Bank | Bankeinzahlungen |
| 1360 | Geldtransit | Kasse-Bank-Transfer |
| 1590 | Durchl. Posten | Gutscheinverkauf (0 %) |
| 1800 | Privatentnahme | Wechselgeld heraus |
| 1890 | Privateinlage | Wechselgeld hinein |
| 3300 | Wareneingang 7 % | Lebensmitteleinkauf |
| 3400 | Wareneingang 19 % | Getränke, Verpackung |
| 4930 | Bürobedarf | Kassenrollen, Stifte |
| 4980 | Sonstige Kosten | Reinigung usw. |
| 8100 | Erlöse 7 % | Speisenverkauf |
| 8300 | Erlöse 19 % | Getränkeverkauf |

### BU-Schlüssel (DATEV)

| Code | Bedeutung |
|------|----------|
| 2 | 7 % Umsatzsteuer |
| 3 | 19 % Umsatzsteuer |
| 8 | 7 % Vorsteuer |
| 9 | 19 % Vorsteuer |
| 40 | Ohne Umsatzsteuer |

---

## Build & Run

```bash
# Desktop starten
./gradlew :composeApp:run

# Android kompilieren
./gradlew :composeApp:compileDebugKotlinAndroid

# Desktop-Distribution erstellen (MSI / DMG / DEB)
./gradlew :composeApp:packageMsi        # Windows
./gradlew :composeApp:packageDmg        # macOS
./gradlew :composeApp:packageDeb        # Linux
```

| Eigenschaft | Wert |
|-------------|------|
| Kotlin | 2.2.20 |
| Compose Multiplatform | 1.10.1 |
| Min SDK (Android) | 30 (Android 11) |
| Target SDK (Android) | 35 (Android 15) |
| JVM Target | 21 |

---

## Hinweise

- **Dies ist kein Ersatz für eine Steuerberatung.** Alle Einstellungen und Exporte mit dem Steuerberater abstimmen.
- Berater-Nr. und Mandanten-Nr. für den DATEV-Export vom Steuerberater erfragen.
- Z-Berichte und alle Belege 10 Jahre aufbewahren (§ 147 AO).
- Die App funktioniert vollständig offline — kein Internet erforderlich.

---

## Lizenz

Dieses Projekt steht unter der [MIT-Lizenz](LICENSE).
