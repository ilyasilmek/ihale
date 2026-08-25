package com.example.domain.engine

data class LegislationItem(
    val id: String,
    val lawCode: String, // e.g. "4734", "4735", "YÖNETMELİK"
    val title: String,
    val articleNumber: String,
    val category: String,
    val summaryText: String,
    val fullText: String,
    val kikGuidanceNotes: String,
    val effectiveDate: String = "01.01.2003 (Güncel Değişiklikler İşlenmiş)"
)

object LegislationDatabase {

    val articles = listOf(
        LegislationItem(
            id = "LEG-4734-12",
            lawCode = "4734 Sayılı KİK",
            title = "Şartnameler ve Marka/Model Belirtme Yasağı",
            articleNumber = "Madde 12",
            category = "Teknik Şartname & Rekabet",
            summaryText = "İhale konusu mal veya hizmet alımları ile yapım işlerinin teknik kriterleri teknik şartnamelerde belirtilir. Belirli bir marka, model, patent, menşei, kaynak veya ürün belirtilemez ve belirli bir marka veya modele yönelik özellik ve tanımlamalara yer verilemez.",
            fullText = """
                MADDE 12- İhale konusu mal veya hizmet alımları ile yapım işlerinin teknik kriterlerine ihale dokümanının bir parçası olan teknik şartnamelerde yer verilir. Belirlenecek teknik kriterler, verimliliği ve fonksiyonelliği sağlamaya yönelik olacak, rekabeti engelleyici hususlar içermeyecek ve bütün istekliler için fırsat eşitliğini sağlayacaktır.
                
                Teknik şartnamelerde, varsa ulusal ve/veya uluslararası teknik standartlara uygunluğu sağlamaya yönelik düzenlemeler de yapılır. Bu şartnamelerde teknik özelliklere ve tanımlamalara yer verilir. Belli bir marka, model, patent, menşei, kaynak veya ürün belirtilemez ve belirli bir marka veya modele yönelik özellik ve tanımlamalara yer verilemez.
                
                Ancak, ulusal ve/veya uluslararası teknik standartların bulunmaması veya teknik özelliklerin belirlenmesinin mümkün olmaması hallerinde "veya dengi" ifadesine yer verilmek şartıyla marka veya model belirtilebilir.
            """.trimIndent(),
            kikGuidanceNotes = "KİK ve Danıştay İçtihadı: Şartnamede marka adı yazılıp 'veya dengi/muadili' yazılmaması doğrudan ihalenin iptali sebebidir. Marka yazılacaksa da standartların bulunmadığı idarece teknik gerekçeyle belgelenmelidir."
        ),
        LegislationItem(
            id = "LEG-4734-10",
            lawCode = "4734 Sayılı KİK",
            title = "İhaleye Katılımda Yeterlik Kuralları",
            articleNumber = "Madde 10",
            category = "Yeterlik & Belgeler",
            summaryText = "İhaleye katılacak isteklilerin ekonomik, mali, mesleki ve teknik yeterliklerinin belirlenmesine ilişkin düzenlemeler İdari Şartname ve İhale İlanında yapılır. Teknik Şartnamelere yeterlik kriteri konulamaz.",
            fullText = """
                MADDE 10- İhaleye katılacak isteklilerden, ekonomik ve malî yeterlik ile mesleki ve teknik yeterliklerinin belirlenmesine ilişkin olarak aşağıda belirtilen bilgi ve belgeler istenebilir:
                a) Ekonomik ve malî yeterliğin belirlenmesi için: Banka referans mektubu, bilanço veya gelir tablosu, iş hacmini gösteren belgeler.
                b) Mesleki ve teknik yeterliğin belirlenmesi için: İş deneyim belgeleri, yetkili satıcılık veya imalatçı belgeleri, kalite yönetim sistem belgeleri (ISO vb.).
            """.trimIndent(),
            kikGuidanceNotes = "Teknik Şartnamede yalnızca mal veya işin fiziksel/teknik performans özellikleri yer alabilir. 'İstekli ISO belgesini teklif zarfında sunacaktır' şeklindeki bir ibare Teknik Şartnamede yer alırsa hükümsüzdür ve KİK nezdinde iptal gerekçesidir."
        ),
        LegislationItem(
            id = "LEG-4734-5",
            lawCode = "4734 Sayılı KİK",
            title = "Temel İlkeler (Saydamlık, Rekabet, Eşit Muamele)",
            articleNumber = "Madde 5",
            category = "Temel Esaslar",
            summaryText = "İdareler, ihalelerde saydamlığı, rekabeti, eşit muameleyi, güvenirliği, gizliliği, kamuoyu denetimini, ihtiyaçların uygun şartlarla ve zamanında karşılanmasını ve kaynakların verimli kullanılmasını sağlamakla sorumludur.",
            fullText = """
                MADDE 5- İdareler, bu Kanuna göre yapılacak ihalelerde; saydamlığı, rekabeti, eşit muameleyi, güvenirliği, gizliliği, kamuoyu denetimini, ihtiyaçların uygun şartlarla ve zamanında karşılanmasını ve kaynakların verimli kullanılmasını sağlamakla sorumludur.
                
                Aralarında kabul edilebilir doğal bir bağlantı olmadığı sürece mal alımı, hizmet alımı ve yapım işleri bir arada ihale edilemez.
                Ödeneği bulunmayan hiçbir iş için ihaleye çıkılamaz.
            """.trimIndent(),
            kikGuidanceNotes = "Dokümanlar arası tutarsızlıklar (örneğin ilan ile idari şartnamede teslim sürelerinin farklı olması) istekliler arasında eşit muamele ve saydamlık ilkesine doğrudan aykırılık oluşturur."
        ),
        LegislationItem(
            id = "LEG-4734-33",
            lawCode = "4734 Sayılı KİK",
            title = "Geçici Teminat Oranı",
            articleNumber = "Madde 33",
            category = "Teminatlar",
            summaryText = "İhalelerde, teklif edilen bedelin %3'ünden az olmamak üzere, istekli tarafından verilecek tutarda geçici teminat alınır.",
            fullText = """
                MADDE 33- İhalelerde, teklif edilen bedelin % 3'ünden az olmamak üzere, istekli tarafından verilecek tutarda geçici teminat alınır. Danışmanlık hizmeti ihalelerinde geçici teminat alınması zorunlu değildir.
            """.trimIndent(),
            kikGuidanceNotes = "İdari şartnamede %3'ün altında (ör. %2) geçici teminat belirlenmesi kanun maddesine doğrudan aykırıdır."
        ),
        LegislationItem(
            id = "LEG-4735-6",
            lawCode = "4735 Sayılı Sözleşmeler Kanunu",
            title = "Sözleşmede Yer Alması Zorunlu Hususlar ve Cezalar",
            articleNumber = "Madde 6 & 7",
            category = "Sözleşme Tasarısı",
            summaryText = "Sözleşmelerde işin süresi, gecikme cezası, teminat şartları, muayene ve kabul koşulları ile fiyat farkı esaslarının yer alması zorunludur.",
            fullText = """
                MADDE 6 & 7- Bu Kanuna göre düzenlenecek sözleşmelerde:
                - İşin adı, niteliği, türü ve miktarı,
                - Sözleşme bedeli ve ödeme şartları,
                - İşe başlama ve bitirme tarihi, gecikme halinde alınacak cezalar,
                - Garanti süresi ve şartları,
                - Muayene ve kabul işlemlerine ilişkin şartlar açıkça belirtilmelidir.
            """.trimIndent(),
            kikGuidanceNotes = "İdari Şartnamede öngörülen gecikme cezası oranı ile Sözleşme Tasarısında yer alan ceza oranının birebir aynı olması zorunludur."
        ),
        LegislationItem(
            id = "LEG-EKAP-YONETMELIK",
            lawCode = "Elektronik İhale Uygulama Yönetmeliği",
            title = "E-Teklif, E-Anahtar ve Yeterlik Bilgileri Tablosu",
            articleNumber = "Yönetmelik Hükümleri",
            category = "EKAP Süreçleri",
            summaryText = "EKAP üzerinden yapılan e-ihalelerde teklifler e-imza ile imzalanır ve e-anahtar ile şifrelenir. İstekliler Yeterlik Bilgileri Tablosunu doldurur.",
            fullText = """
                Elektronik Kamu Alımları Platformu (EKAP) üzerinden gerçekleştirilen e-ihalelerde:
                1. İstekliler e-tekliflerini e-imza ile imzalayarak EKAP'a yükler.
                2. Teklifler isteklinin oluşturduğu e-anahtar ile şifrelenir ve ihale açılış saatinde e-anahtar sisteme gönderilir.
                3. Yeterlik değerlendirmesi Yeterlik Bilgileri Tablosu üzerinden elektronik ortamda yürütülür.
            """.trimIndent(),
            kikGuidanceNotes = "Fiziksel teklif zarfı veya ıslak imza talep eden şartname maddeleri e-ihalelerde hükümsüzdür."
        )
    )
}
