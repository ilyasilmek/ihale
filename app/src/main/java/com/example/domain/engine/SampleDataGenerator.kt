package com.example.domain.engine

import com.example.domain.model.CustomRuleType
import com.example.domain.model.DocumentType
import com.example.domain.model.TenderProcedure
import com.example.domain.model.TenderType
import com.example.domain.model.UserCustomRule
import java.util.UUID

data class SampleTenderPackage(
    val title: String,
    val tenderNumber: String,
    val tenderType: TenderType,
    val tenderProcedure: TenderProcedure,
    val institutionName: String,
    val description: String,
    val documents: List<SampleDocument>
)

data class SampleDocument(
    val name: String,
    val type: DocumentType,
    val content: String
)

object SampleDataGenerator {

    fun getSampleTenderPackages(): List<SampleTenderPackage> {
        return listOf(
            SampleTenderPackage(
                title = "TCDD Ray Bakım ve Sinyalizasyon Donanım Alımı",
                tenderNumber = "2026/04812",
                tenderType = TenderType.GOODS,
                tenderProcedure = TenderProcedure.OPEN,
                institutionName = "TCDD Genel Müdürlüğü Satın Alma Dairesi",
                description = "Gerçekçi çelişkiler içeren demo ihale paketi: 24 ay vs 12 ay garanti çelişkisi, 'Siemens' marka yazımı (veya dengi yok), teknik şartnamede ISO belgesi istenmesi ve 10 vs 12 adet teklif cetveli uyuşmazlığı.",
                documents = listOf(
                    SampleDocument(
                        name = "İhale İlanı.txt",
                        type = DocumentType.TENDER_NOTICE,
                        content = """
                            T.C. DEVLET DEMİRYOLLARI İŞLETMESİ GENEL MÜDÜRLÜĞÜ
                            İHALE İLANI
                            İhale Kayıt Numarası (İKN): 2026/04812
                            
                            1. İdarenin:
                            a) Adı: TCDD Genel Müdürlüğü Satın Alma Dairesi Başkanlığı
                            b) Adresi: Hipodrom Caddesi No:3 Gar / ANKARA
                            c) Telefon ve faks numarası: 0312 309 05 15
                            
                            2. İhale Konusu Malın:
                            a) Adı, niteliği, türü ve miktarı: 10 (on) kalem Ray Bakım ve Sinyalizasyon Donanımı Alımı
                            b) Teslim yeri: TCDD Gebze Vagon Bakım Atölye Müdürlüğü
                            c) Teslim süresi: İşe başlama tarihinden itibaren 30 takvim günü içinde teslim edilecektir.
                            
                            3. İhalenin:
                            a) Yapılacağı yer: TCDD Genel Müdürlüğü Toplantı Salonu (EKAP E-Teklif)
                            b) Tarihi ve saati: 25.09.2026 - 10:00
                            
                            4. İhaleye Katılabilme Şartları:
                            İsteklilerin 4734 Sayılı Kamu İhale Kanunu ve ilgili mevzuat uyarınca e-tekliflerini EKAP üzerinden e-imza ile göndermeleri gerekmektedir.
                        """.trimIndent()
                    ),
                    SampleDocument(
                        name = "İdari Şartname.txt",
                        type = DocumentType.ADMINISTRATIVE_SPEC,
                        content = """
                            T.C. TCDD İŞLETMESİ GENEL MÜDÜRLÜĞÜ
                            RAY BAKIM VE SİNYALİZASYON DONANIMI ALIMI İDARİ ŞARTNAMESİ
                            
                            Madde 1 - İhale Konusu İşe İlişkin Bilgiler
                            1.1. İhale konusu malın adı: Ray Bakım ve Sinyalizasyon Donanımı Alımı.
                            
                            Madde 2 - Tekliflerin Sunulma Şekli ve EKAP Süreçleri
                            2.1. Teklifler EKAP üzerinden e-imza kullanılarak hazırlanacak ve e-anahtar ile şifrelenerek ihale saatine kadar gönderilecektir.
                            2.2. İstekliler Yeterlik Bilgileri Tablosu düzenleyecektir.
                            
                            Madde 3 - Geçici Teminat
                            3.1. İstekliler teklif ettikleri bedelin %3 (yüzde üç) oranından az olmamak üzere kendi belirleyecekleri tutarda geçici teminat vereceklerdir.
                            
                            Madde 4 - Teslim Süresi ve Şartları
                            4.1. Malların tamamı sözleşmenin imzalanmasına müteakip 45 takvim günü içinde teslim edilecektir.
                            
                            Madde 5 - Garanti Şartları
                            5.1. Teslim edilen tüm donanımlar kabul tarihinden itibaren en az 12 ay süreli garanti kapsamında olacaktır.
                            
                            Madde 6 - Gecikme Cezası
                            6.1. İdarenin gecikme halinde uygulayacağı ceza günlük sözleşme bedelinin %0.05 (onbinde beş) oranındadır.
                        """.trimIndent()
                    ),
                    SampleDocument(
                        name = "Teknik Şartname.txt",
                        type = DocumentType.TECHNICAL_SPEC,
                        content = """
                            TCDD GENEL MÜDÜRLÜĞÜ TEKNİK ŞARTNAMESİ
                            BÖLÜM I - GENEL TEKNİK KRİTERLER
                            
                            Madde 1 - Kapsam ve Donanım Özellikleri
                            1.1. Bu şartname TCDD bünyesinde kullanılacak hat sinyal kontrol üniteleri ve ray izleme sensörlerini kapsar.
                            1.2. Sinyal anahtar modülleri Siemens S7-1500 PLC kontrol ünitesi ile tam uyumlu ve aynı donanım mimarisinde olacaktır.
                            1.3. Cihazların çalışma sıcaklık aralığı -20°C ile +60°C arasında olmalıdır.
                            
                            Madde 2 - Garanti ve Servis Koşulları
                            2.1. İhale konusu tüm sinyalizasyon cihazları kabul tarihinden itibaren 24 ay süreli üretici ve yüklenici garantisine sahip olacaktır.
                            2.2. Arıza durumunda en geç 24 saat içinde müdahale edilmelidir.
                            
                            Madde 3 - Sunulacak Yeterlik Belgeleri
                            3.1. İsteklinin yetkili satıcı belgesi ve ISO 9001 belgesi teklif aşamasında teklif dosyası ekinde idareye sunulmalıdır.
                            3.2. Malzemelerin teslimatı sırasında orjinal labaratuar test raporları verilecektir.
                        """.trimIndent()
                    ),
                    SampleDocument(
                        name = "Sözleşme Tasarısı.txt",
                        type = DocumentType.CONTRACT_DRAFT,
                        content = """
                            MAL ALIMINA AİT TİP SÖZLEŞME TASARISI
                            
                            Madde 1 - Sözleşmenin Tarafları
                            Bu sözleşme TCDD Genel Müdürlüğü ile Yüklenici arasında akdedilmiştir.
                            
                            Madde 2 - İşin Süresi ve Teslim Yeri
                            2.1. Sözleşme konusu mallar 45 takvim günü içinde TCDD depolarına teslim edilecektir.
                            
                            Madde 3 - Garanti ve Taahhütler
                            3.1. Malların kabul tarihinden itibaren 12 ay süreli garantisi bulunmaktadır.
                            
                            Madde 4 - Cezalar
                            4.1. Yüklenicinin taahhüdünü süresinde yerine getirmemesi halinde her takvim günü için sözleşme bedelinin %0.05 oranında gecikme cezası kesilir.
                        """.trimIndent()
                    ),
                    SampleDocument(
                        name = "Birim Fiyat Teklif Cetveli.txt",
                        type = DocumentType.PRICE_SCHEDULE,
                        content = """
                            BİRİM FİYAT TEKLİF CETVELİ
                            İhale Kayıt No: 2026/04812
                            
                            Sıra No: 1 - Ray Hat Kontrol Sensörü - Miktar: 12 Adet
                            Sıra No: 2 - Sinyalizasyon Anahtar Modülü - Miktar: 10 Adet
                            Sıra No: 3 - Güç Dağıtım ve Akü Panosu - Miktar: 4 Adet
                            Sıra No: 4 - Fiber Optik İletişim Dönüştürücü - Miktar: 8 Adet
                            Sıra No: 5 - Hat Sonu Sonlandırma Direnç Seti - Miktar: 20 Adet
                            Sıra No: 6 - Topraklama ve Parafudr Koruma Ünitesi - Miktar: 10 Adet
                            Sıra No: 7 - İzole Ray Bağlantı Klemensi - Miktar: 100 Adet
                            Sıra No: 8 - Sinyal Lambası LED Matris Modülü - Miktar: 16 Adet
                            Sıra No: 9 - Manuel Bypass Buton İstasyonu - Miktar: 6 Adet
                            Sıra No: 10 - Acil Durum Stop Röle Kiti - Miktar: 10 Adet
                            Sıra No: 11 - Hat İzleme Yazılımı Kullanıcı Lisansı - Miktar: 2 Adet
                            Sıra No: 12 - Saha Montaj ve Devreye Alma Kiti - Miktar: 1 Paket
                        """.trimIndent()
                    )
                )
            ),
            SampleTenderPackage(
                title = "Şehir Hastanesi Tıbbi Cihaz ve Biyomedikal Sarf Alımı",
                tenderNumber = "2026/08194",
                tenderType = TenderType.GOODS,
                tenderProcedure = TenderProcedure.OPEN,
                institutionName = "İl Sağlık Müdürlüğü",
                description = "KVKK hassas verileri (TC No, IBAN), yetersiz teminat oranı (%2), eksik 'veya dengi' Philips marka yazımı ve resmi yazım hataları içeren sağlık ihaleleri paketi.",
                documents = listOf(
                    SampleDocument(
                        name = "İdari Şartname_Saglik.txt",
                        type = DocumentType.ADMINISTRATIVE_SPEC,
                        content = """
                            İL SAĞLIK MÜDÜRLÜĞÜ
                            HASTANE BİYOMEDİKAL CİHAZ ALIMI İDARİ ŞARTNAMESİ
                            
                            Madde 1 - İhale Komisyonu İletişim Bilgileri
                            Komisyon Başkanı Dr. Ahmet Yılmaz T.C. Kimlik No: 12345678901, İrtibat Tel: 0532 555 12 34
                            Muhasebe ve Teminat İade Hesabı: TR33 0006 1005 1234 5678 9012 34
                            
                            Madde 2 - Geçici Teminat
                            2.1. İstekliler teklif tutarının %2 (yüzde iki) oranında geçici teminat mektubu sunacaklardır.
                            
                            Madde 3 - Teslim Süresi
                            3.1. Cihazlar 60 takvim günü içinde hastane deposuna teslim edilmelidir.
                        """.trimIndent()
                    ),
                    SampleDocument(
                        name = "Teknik Şartname_Saglik.txt",
                        type = DocumentType.TECHNICAL_SPEC,
                        content = """
                            HASTANE TIBBİ CİHAZ TEKNİK ŞARTNAMESİ
                            
                            Madde 1 - Hasta Başı Monitör Kriterleri
                            1.1. Monitörler Philips IntelliVue serisi donanımla aynı soket yapısına ve EKG dalga boyuna sahip olacaktır.
                            1.2. Ekran boyutu en az 15 inç olmalıdır.
                            1.3. Cihazlar için 36 ay garanti verilecektir.
                            1.4. İstekliler cihazlara ait kalibrasyon belgelerini ve labaratuar onaylarını sunmalıdır.
                        """.trimIndent()
                    )
                )
            )
        )
    }

    fun getDefaultUserRules(): List<UserCustomRule> {
        return listOf(
            UserCustomRule(
                id = UUID.randomUUID().toString(),
                name = "Kurum Adı Standartlaştırma",
                description = "Şartnamelerde kurum adı tam ve resmi unvanıyla yer almalıdır.",
                ruleType = CustomRuleType.INSTITUTION_NAME_MUST_MATCH,
                targetText = "Gebze Vagon Bakım Atölye Müdürlüğü",
                isEnabled = true
            ),
            UserCustomRule(
                id = UUID.randomUUID().toString(),
                name = "Standart Tarih Formatı (GG.AA.YYYY)",
                description = "Tarihlerde / veya - yerine . kullanılmalıdır.",
                ruleType = CustomRuleType.DATE_FORMAT_MANDATE,
                targetText = "GG.AA.YYYY",
                isEnabled = true
            ),
            UserCustomRule(
                id = UUID.randomUUID().toString(),
                name = "Kuruluş Terimi Tercihi",
                description = "\"Şirket\" yerine kamu metinlerinde \"Kuruluş\" veya \"İstekli\" tercih edilmelidir.",
                ruleType = CustomRuleType.SPELLING_REPLACE,
                targetText = "şirket",
                replacementText = "istekli / kuruluş",
                isEnabled = false
            )
        )
    }
}
