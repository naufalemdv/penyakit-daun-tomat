package com.example.model

object DiseaseRepository {
    val DISEASES: List<DiseaseInfo> = listOf(
        DiseaseInfo(
            id = "tomato_healthy",
            displayName = "Sehat",
            shortName = "Sehat",
            scientificName = "Solanum lycopersicum",
            isHealthy = true,
            fieldAction = "Perawatan Rutin & Pantau",
            symptoms = "Daun hijau segar cerah, permukaan rata tanpa bercak klorosis maupun nekrosis, lamina daun kokoh dan pertumbuhan vegetatif normal.",
            triggers = "Kondisi kebun optimal, aerasi memadai, sinar matahari cukup, dan keseimbangan hara N-P-K terjaga.",
            handling = "Lanjutkan penyiraman teratur pada pangkal batang (hindari membasahi daun), beri pupuk organik/kompos matang, serta pantau rutin setiap pagi.",
            reference = "Balai Penelitian Tanaman Sayuran (Balitsa), Badan Litbang Pertanian."
        ),
        DiseaseInfo(
            id = "tomato_late_blight",
            displayName = "Busuk Daun (Late Blight)",
            shortName = "Busuk Daun",
            scientificName = "Phytophthora infestans",
            isHealthy = false,
            fieldAction = "Isolasi & Fungisida",
            symptoms = "Bercak basah keabu-abuan berubah cepat menjadi cokelat gelap atau kehitaman. Pada kondisi lembap, permukaan bawah daun ditutupi lapisan spora putih tipis seperti beludru.",
            triggers = "Suhu sejuk (15–22°C), kelembapan relatif tinggi (>90%), hujan berkepanjangan atau embun malam lebat.",
            handling = "Segera pangkas dan musnahkan daun terinfeksi (jangan jadikan kompos). Kurangi kelembapan tajuk dengan perompesan cabang air. Aplikasikan fungisida protektif berbahan aktif mankozeb atau simoksanil.",
            reference = "Departemen Proteksi Tanaman, Institut Pertanian Bogor (IPB) & Balitsa."
        ),
        DiseaseInfo(
            id = "tomato_early_blight",
            displayName = "Bercak Kering (Early Blight)",
            shortName = "Bercak Kering",
            scientificName = "Alternaria solani",
            isHealthy = false,
            fieldAction = "Pangkas Daun Tua & Sanitasi",
            symptoms = "Bercak cokelat gelap dengan pola cincin melingkar konsentris menyerupai papan sasaran (target board ring). Daun di sekitar bercak menguning (klorosis).",
            triggers = "Cuaca hangat (24–30°C) diselingi hujan lebat, tanaman mengalami stres nutrisi, dan aerasi daun bawah buruk.",
            handling = "Pangkas daun-daun tua di bagian bawah yang berdekatan dengan tanah. Lakukan rotasi tanaman dengan non-Solanaceae. Aplikasikan mulsa plastik dan fungisida klorotalonil/difenokonazol.",
            reference = "Plant Disease Diagnostic Clinic, Cornell University Extension."
        ),
        DiseaseInfo(
            id = "tomato_bacterial_spot",
            displayName = "Bercak Bakteri (Bacterial Spot)",
            shortName = "Bercak Bakteri",
            scientificName = "Xanthomonas perforans / campestris",
            isHealthy = false,
            fieldAction = "Bakterisida Tembaga + Sanitasi",
            symptoms = "Bercak kecil berair (1–3 mm) berwarna gelap dengan tepi bersudut, mengering menjadi bercak cekung cokelat kehitaman dengan halo kuning mengelilinginya.",
            triggers = "Hujan disertai angin kencang, percikan air irigasi sprinkler, dan suhu udara hangat (25–30°C).",
            handling = "Gunakan benih bersertifikat bebas patogen. Hindari aktivitas di kebun saat tajuk tanaman masih basah untuk mencegah penyebaran bakteri. Semprot bakterisida tembaga hidroksida yang dicampur mankozeb.",
            reference = "University of Florida IFAS Extension Plant Pathology."
        ),
        DiseaseInfo(
            id = "tomato_leaf_mold",
            displayName = "Kapang Daun (Leaf Mold)",
            shortName = "Kapang Daun",
            scientificName = "Passalora fulva (Cladosporium fulvum)",
            isHealthy = false,
            fieldAction = "Ventilasi Maksimal & Fungisida",
            symptoms = "Bercak kuning pucat dengan batas tidak tegas pada permukaan atas daun. Sisi bawah daun tertutup lapisan massa jamur seperti beludru berwarna hijau zaitun hingga cokelat.",
            triggers = "Kelembapan relatif sangat tinggi (>85%), temperatur 20–25°C, dan ventilasi udara minim pada rumah kaca/bedengan rapat.",
            handling = "Buka ventilasi naungan atau green house untuk menurunkan kelembapan relatif. Jarangkan tajuk dengan membuang daun yang terlalu rimbun. Semprot fungisida tembaga atau azoksistrobin bila parah.",
            reference = "Ohio State University Extension, Agriculture and Natural Resources."
        ),
        DiseaseInfo(
            id = "tomato_septoria_leaf_spot",
            displayName = "Bercak Daun Septoria",
            shortName = "Septoria",
            scientificName = "Septoria lycopersici",
            isHealthy = false,
            fieldAction = "Pasang Mulsa & Buang Daun Bawah",
            symptoms = "Bercak melingkar kecil (2–4 mm) berjumlah banyak dengan pusat berwarna abu-abu/putih dan tepian cokelat tua. Di bagian tengah tampak bintik hitam kecil (piknidia).",
            triggers = "Periode basah hangat berkepanjangan dan percikan air hujan dari permukaan tanah langsung ke dedaunan bawah.",
            handling = "Gunakan mulsa jerami atau plastik perak untuk menahan percikan tanah. Buang dan bakar daun yang terinfeksi di awal serangan. Aplikasikan fungisida mankozeb secara rutin.",
            reference = "Penn State Extension, College of Agricultural Sciences."
        ),
        DiseaseInfo(
            id = "tomato_spider_mites_twospotted_spider_mite",
            displayName = "Tungau Laba-laba (Spider Mites)",
            shortName = "Tungau",
            scientificName = "Tetranychus urticae",
            isHealthy = false,
            fieldAction = "Akarisida & Semprot Bawah Daun",
            symptoms = "Bintik-bintik kuning keperakan sangat halus (stippling) di permukaan atas daun, daun menjadi kusam perunggu, dan terdapat jaring benang laba-laba halus di bawah daun.",
            triggers = "Kondisi cuaca kering, panas terik (>30°C), lingkungan berdebu, dan penggunaan insektisida kimia yang mematikan predator alami.",
            handling = "Semprot permukaan bawah daun dengan semburan air bersih bertekanan untuk merusak koloni tungau. Gunakan minyak mimba (neem oil) atau akarisida berbahan aktif abamektin.",
            reference = "UMass Amherst Center for Agriculture, Greenhouse Crops and Floriculture."
        ),
        DiseaseInfo(
            id = "tomato_target_spot",
            displayName = "Bercak Target (Target Spot)",
            shortName = "Bercak Target",
            scientificName = "Corynespora cassiicola",
            isHealthy = false,
            fieldAction = "Fungisida Sistemik & Jarak Tanam",
            symptoms = "Bercak melingkar cokelat dengan cincin konsentris tegas dan tepi tajam. Daun yang terserang parah mudah menguning dan rontok sebelum waktunya.",
            triggers = "Kelembapan udara tinggi pada malam hari dan suhu hangat (20–28°C).",
            handling = "Perlebar jarak tanam untuk sirkulasi udara optimal. Bersihkan sisa-sisa gulma di sekitar bedengan. Lakukan rotasi fungisida sistemik golongan triazol atau strobilurin.",
            reference = "University of Florida Extension Plant Pathology."
        ),
        DiseaseInfo(
            id = "tomato_tomato_mosaic_virus",
            displayName = "Virus Mosaik (Mosaic Virus)",
            shortName = "Virus Mosaik",
            scientificName = "Tomato Mosaic Virus (ToMV)",
            isHealthy = false,
            fieldAction = "Eradikasi Tanaman Tertular",
            symptoms = "Daun menampilkan belang hijau tua dan hijau muda (mosaik), permukaan daun bergelombang mengerut, dan pada kasus ekstrem daun menyempit mirip tali sepatu.",
            triggers = "Penularan mekanis melalui kontak tangan pekerja, perkakas gunting pangkas, benih terinfeksi, atau rokok.",
            handling = "Cabut dan musnahkan tanaman yang terinfeksi untuk memutus rantai penularan. Cuci tangan dengan sabun/detergen dan sterilkan alat pangkas dengan larutan pemutih 10%.",
            reference = "American Phytopathological Society (APS) Plant Disease Lessons."
        ),
        DiseaseInfo(
            id = "tomato_tomato_yellow_leaf_curl_virus",
            displayName = "Virus Kuning Keriting (TYLCV)",
            shortName = "Kuning Keriting",
            scientificName = "Tomato Yellow Leaf Curl Virus",
            isHealthy = false,
            fieldAction = "Kendalikan Kutu Kebul Vektor",
            symptoms = "Daun muda mengerut ke atas membentuk mangkuk (cupping), tepi daun menguning jelas (klorosis marginal), dan pertumbuhan pucuk tanaman terhenti menjadi kerdil.",
            triggers = "Populasi vektor kutu kebul (Bemisia tabaci) yang melonjak saat musim kemarau atau cuaca panas kering.",
            handling = "Pasang perangkap lekat kuning (yellow sticky trap) di sela bedengan. Kendalikan populasi vektor kutu kebul dengan insektisida imidakloprid. Pasang jaring serangga pada pembibitan.",
            reference = "World Vegetable Center (AVRDC) & Kementan RI."
        )
    )

    fun getById(id: String): DiseaseInfo {
        return DISEASES.find { it.id == id } ?: DISEASES[1] // default late blight
    }
}
