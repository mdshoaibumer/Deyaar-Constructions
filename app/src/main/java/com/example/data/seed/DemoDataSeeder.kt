package com.example.data.seed

import com.example.data.local.AppDatabase
import com.example.data.local.entity.*
import com.example.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import java.util.Calendar

/**
 * Seeds the database with realistic construction company demo data.
 * Only seeds if the database is empty (first launch).
 */
class DemoDataSeeder(private val database: AppDatabase) {

    suspend fun seedIfEmpty() = withContext(Dispatchers.IO) {
        val projectCount = database.projectDao().getProjectsCount()
        if (projectCount > 0) return@withContext // Already has data

        seedClients()
        seedProjects()
        seedWorkers()
        seedMaterials()
        seedSuppliers()
        seedTransactions()
        seedAttendance()
        seedPhotos()
        seedMilestones()
        seedResourceAllocations()
        seedDocuments()
    }

    private val now = System.currentTimeMillis()
    private val day = 86_400_000L
    private val clientIds = (1..20).map { "client_${UUID.randomUUID().toString().take(8)}" }
    private val projectIds = (1..50).map { "proj_${UUID.randomUUID().toString().take(8)}" }
    private val workerIds = (1..150).map { "worker_${UUID.randomUUID().toString().take(8)}" }

    private suspend fun seedClients() {
        val clients = listOf(
            c("Rajesh Kumar", "9876543210", "Kumar Builders Pvt Ltd", "Sector 45, Noida"),
            c("Amit Sharma", "9812345678", "Sharma Constructions", "DLF Phase 3, Gurgaon"),
            c("Priya Patel", "9898765432", "Patel Enterprises", "Andheri West, Mumbai"),
            c("Mohammed Ali", "9945612378", "Ali Real Estate", "Banjara Hills, Hyderabad"),
            c("Sunita Devi", "9756483210", "Devi Housing", "Jayanagar, Bangalore"),
            c("Vikram Singh", "9632587410", "Singh Infrastructure", "Model Town, Delhi"),
            c("Anita Reddy", "9874563210", "Reddy Developers", "Jubilee Hills, Hyderabad"),
            c("Deepak Joshi", "9123456780", "Joshi & Sons", "Koregaon Park, Pune"),
            c("Kavita Nair", "9567890123", "Nair Properties", "Kochi Marine Drive"),
            c("Suresh Menon", "9234567891", "Menon Group", "Thrissur, Kerala"),
            c("Ravi Gupta", "9345678912", "Gupta Construction Co", "Laxmi Nagar, Delhi"),
            c("Meera Iyer", "9456789023", "Iyer Associates", "T Nagar, Chennai"),
            c("Arun Khanna", "9567891234", "Khanna Infra", "Chandigarh Sector 17"),
            c("Neha Kapoor", "9678902345", "Kapoor Estates", "Colaba, Mumbai"),
            c("Sanjay Mishra", "9789013456", "Mishra Builders", "Gomti Nagar, Lucknow"),
            c("Lakshmi Rao", "9890124567", "Rao Constructions", "Whitefield, Bangalore"),
            c("Prakash Jain", "9901235678", "Jain Housing", "Vaishali Nagar, Jaipur"),
            c("Divya Sharma", "9012346789", "DS Projects", "Aundh, Pune"),
            c("Manoj Tiwari", "9112345670", "Tiwari Builders", "Gomti Nagar, Lucknow"),
            c("Pooja Agarwal", "9223456781", "Agarwal Properties", "Salt Lake, Kolkata")
        )
        database.clientDao().insertClients(
            clients.mapIndexed { i, client -> client.copy(id = clientIds[i]) }
        )
    }

    private fun c(name: String, phone: String, company: String, address: String) = ClientEntity(
        id = "", name = name, companyName = company, phone = phone,
        email = "${name.lowercase().replace(" ", ".")}@email.com",
        address = address, notes = null, createdAt = now - (30 * day),
        updatedAt = now, altPhone = null, whatsapp = phone, gstNumber = null,
        panNumber = null, city = address.split(",").lastOrNull()?.trim(),
        state = null, pincode = null, mapsLocation = null,
        category = ClientCategory.RESIDENTIAL, photoPath = null,
        isActive = true, isFavorite = false
    )

    private suspend fun seedProjects() {
        val categories = ProjectCategory.entries.toTypedArray()
        val statuses = listOf(ProjectStatus.ACTIVE, ProjectStatus.ACTIVE, ProjectStatus.ACTIVE,
            ProjectStatus.PLANNING, ProjectStatus.COMPLETED, ProjectStatus.COMPLETED, ProjectStatus.ON_HOLD)
        val names = listOf(
            "Sunrise Villa - Phase 1", "Greenfield Apartments Tower A", "Metro Mall Extension",
            "Royal Residency Block C", "Tech Park Building 5", "Heritage Renovation - Fort",
            "Lakeside Villas", "Diamond Tower", "Emerald Heights", "Golden Gate Complex",
            "Silver Oaks Residential", "Crystal Palace Hotel", "Horizon Office Park",
            "Blue Ridge Apartments", "Sapphire Towers Phase 2", "Coral Bay Villas",
            "Platinum Plaza Commercial", "Ruby Gardens", "Topaz Residency",
            "Amber Valley Township", "Pearl Harbour Apartments", "Jade Point Offices",
            "Opal Heights", "Garnet Complex", "Ivory Towers Phase 3",
            "Bronze Gate Industrial", "Copper Lane Housing", "Marble Arch Mall",
            "Granite Peak Villas", "Quartz Bay Resort", "Skyline Residences",
            "Palm Grove Villas", "Cedar Heights Apartments", "Oak Park Commercial",
            "Maple Leaf Residency", "Pine Ridge Estate", "Willow Creek Housing",
            "Birch Wood Towers", "Elm Street Complex", "Lotus Pond Villas",
            "Jasmine Gardens Phase 2", "Orchid Heights Tower B", "Tulip Bay Apartments",
            "Sunflower Residency", "Rose Garden Estate", "Daisy View Homes",
            "Lavender Hills", "Marigold Complex", "Iris Point Towers", "Poppy Fields Estate"
        )
        val projects = names.take(50).mapIndexed { i, name ->
            val status = statuses[i % statuses.size]
            val startOffset = (60 + i * 10) * day
            val contractVal = ((50 + i * 15) * 100_000L) * 100 // in paise
            ProjectEntity(
                id = projectIds[i],
                projectNumber = "DEY-2024-${String.format("%03d", i + 1)}",
                name = name,
                clientId = clientIds[i % clientIds.size],
                category = categories[i % categories.size],
                address = "Plot ${i + 1}, Sector ${10 + i}",
                location = "28.${5700 + i},77.${2000 + i}",
                contractValuePaise = contractVal,
                estimatedBudgetPaise = (contractVal * 0.75).toLong(),
                advanceReceivedPaise = (contractVal * 0.3).toLong(),
                expectedProfitPaise = (contractVal * 0.25).toLong(),
                startDate = now - startOffset,
                expectedCompletionDate = now + (180 - i * 5) * day,
                actualCompletionDate = if (status == ProjectStatus.COMPLETED) now - (i * 3) * day else null,
                status = status,
                priority = ProjectPriority.entries[i % ProjectPriority.entries.size],
                engineerInCharge = listOf("Ramesh K.", "Sunil M.", "Anil P.", "Vijay R.")[i % 4],
                notes = "Standard ${categories[i % categories.size].displayName} project",
                progress = when (status) {
                    ProjectStatus.COMPLETED -> 100
                    ProjectStatus.ACTIVE -> 20 + (i * 3) % 60
                    ProjectStatus.PLANNING -> 5
                    else -> 15
                },
                createdAt = now - startOffset,
                updatedAt = now - (i * day)
            )
        }
        database.projectDao().insertProjects(projects)
    }

    private suspend fun seedWorkers() {
        val trades = listOf("Mason", "Carpenter", "Electrician", "Plumber", "Painter",
            "Welder", "Crane Operator", "General Labour", "Tile Setter", "Rebar Worker",
            "Scaffolder", "Excavator Operator", "Concrete Mixer", "Plasterer", "Waterproofer")
        val firstNames = listOf("Raju", "Suresh", "Mohan", "Dinesh", "Ganesh", "Amar", "Vijay",
            "Ashok", "Ramesh", "Gopal", "Kiran", "Manoj", "Nandu", "Pappu", "Qasim",
            "Ratan", "Shyam", "Tarun", "Umesh", "Vinod", "Wasim", "Yasin", "Zaheer",
            "Balram", "Chhotu", "Deepu", "Farhan", "Gopi", "Hari", "Ismail")
        val lastNames = listOf("Yadav", "Patel", "Sharma", "Singh", "Kumar", "Gupta", "Thakur",
            "Mandal", "Das", "Verma", "Chauhan", "Jha", "Mishra", "Tiwari", "Pandey",
            "Rajput", "Meena", "Saini", "Nair", "Reddy", "Khan", "Ansari", "Paswan",
            "Mahto", "Oraon", "Munda", "Gond", "Baiga", "Korwa", "Lohar")

        val workers = (0 until 150).map { i ->
            WorkerEntity(
                id = workerIds[i],
                fullName = "${firstNames[i % firstNames.size]} ${lastNames[i % lastNames.size]}",
                mobileNumber = "9${(100000000 + i * 11111111L) % 900000000 + 100000000}",
                trade = trades[i % trades.size],
                dailyWagePaise = ((500 + (i % 10) * 50) * 100).toLong(), // 500-950 INR
                experience = "${1 + i % 15} years",
                joiningDate = now - (90 + i * 3) * day,
                emergencyContact = "9${(200000000 + i * 22222222L) % 900000000 + 100000000}",
                address = "Village ${firstNames[i % firstNames.size]}pur, District ${lastNames[(i + 5) % lastNames.size]}",
                status = if (i < 130) "ACTIVE" else "INACTIVE",
                createdAt = now - (90 + i * 3) * day,
                updatedAt = now - (i % 10) * day
            )
        }
        database.workerDao().insertWorkers(workers)
    }

    private suspend fun seedMaterials() {
        val materials = listOf(
            mat("OPC Cement 53 Grade", "Cement", "Bags", 450.0, 100.0, 800.0, 38000),
            mat("TMT Steel Bars 12mm", "Steel", "Tonnes", 12.5, 5.0, 25.0, 5500000),
            mat("River Sand (Fine)", "Sand", "Cu.M", 85.0, 30.0, 150.0, 180000),
            mat("Red Bricks (Standard)", "Bricks", "1000 Nos", 35.0, 10.0, 60.0, 650000),
            mat("Vitrified Floor Tiles 2x2", "Tiles", "Boxes", 120.0, 40.0, 200.0, 95000),
            mat("Apex Weatherproof Paint", "Paint", "Litres", 200.0, 50.0, 350.0, 42000),
            mat("PPC Cement (Blended)", "Cement", "Bags", 300.0, 80.0, 500.0, 36000),
            mat("Steel Binding Wire", "Steel", "Kg", 500.0, 100.0, 800.0, 8500),
            mat("M-Sand (Manufactured)", "Sand", "Cu.M", 60.0, 20.0, 100.0, 200000),
            mat("AAC Blocks 600x200x150", "Bricks", "Nos", 2000.0, 500.0, 4000.0, 5500),
            mat("Wall Putty", "Paint", "Bags", 80.0, 20.0, 120.0, 75000),
            mat("Ceramic Wall Tiles", "Tiles", "Boxes", 90.0, 30.0, 150.0, 85000),
            mat("Plywood 18mm BWR", "Wood", "Sheets", 45.0, 15.0, 80.0, 180000),
            mat("Electrical Conduit Pipes", "Electrical", "Metres", 500.0, 100.0, 800.0, 4500),
            mat("CPVC Pipes 1 inch", "Plumbing", "Metres", 300.0, 80.0, 500.0, 12000),
            mat("RMC M25 Grade", "Concrete", "Cu.M", 25.0, 10.0, 50.0, 520000),
            mat("Waterproofing Membrane", "Chemical", "Sq.M", 150.0, 40.0, 250.0, 35000),
            mat("GI Binding Wire 18G", "Steel", "Kg", 200.0, 50.0, 400.0, 9000),
            mat("White Cement", "Cement", "Bags", 50.0, 15.0, 80.0, 65000),
            mat("Granite Slabs", "Stone", "Sq.Ft", 300.0, 80.0, 500.0, 18000),
            // Extended materials to reach 100
            mat("TMT Steel Bars 8mm", "Steel", "Tonnes", 8.0, 3.0, 15.0, 5200000),
            mat("TMT Steel Bars 16mm", "Steel", "Tonnes", 10.0, 4.0, 20.0, 5600000),
            mat("TMT Steel Bars 20mm", "Steel", "Tonnes", 6.0, 2.0, 12.0, 5800000),
            mat("TMT Steel Bars 25mm", "Steel", "Tonnes", 4.0, 2.0, 8.0, 6000000),
            mat("Concrete Blocks 400x200x200", "Bricks", "Nos", 1500.0, 400.0, 3000.0, 4200),
            mat("Fly Ash Bricks", "Bricks", "1000 Nos", 25.0, 8.0, 50.0, 550000),
            mat("Interlocking Paver Blocks", "Bricks", "Sq.M", 200.0, 50.0, 400.0, 75000),
            mat("Coarse Sand (Plaster)", "Sand", "Cu.M", 50.0, 15.0, 80.0, 150000),
            mat("Gravel 20mm", "Sand", "Cu.M", 40.0, 10.0, 70.0, 130000),
            mat("Crushed Stone Aggregate", "Sand", "Cu.M", 60.0, 20.0, 100.0, 140000),
            mat("Portland Slag Cement", "Cement", "Bags", 200.0, 60.0, 350.0, 37000),
            mat("Rapid Setting Cement", "Cement", "Bags", 30.0, 10.0, 50.0, 55000),
            mat("Micro Concrete", "Cement", "Bags", 20.0, 5.0, 40.0, 120000),
            mat("Asian Paints Tractor Emulsion", "Paint", "Litres", 150.0, 40.0, 250.0, 28000),
            mat("Primer Paint (Wood)", "Paint", "Litres", 80.0, 20.0, 150.0, 32000),
            mat("Enamel Paint", "Paint", "Litres", 100.0, 25.0, 180.0, 38000),
            mat("Anti-Corrosion Paint", "Paint", "Litres", 50.0, 15.0, 80.0, 65000),
            mat("Epoxy Floor Coating", "Paint", "Litres", 40.0, 10.0, 70.0, 85000),
            mat("Floor Tiles 600x600", "Tiles", "Boxes", 80.0, 25.0, 140.0, 110000),
            mat("Bathroom Tiles 300x300", "Tiles", "Boxes", 60.0, 20.0, 100.0, 72000),
            mat("Mosaic Tiles", "Tiles", "Boxes", 30.0, 10.0, 50.0, 150000),
            mat("Parking Tiles Heavy Duty", "Tiles", "Boxes", 40.0, 15.0, 70.0, 95000),
            mat("Roof Tiles (Mangalore)", "Tiles", "Nos", 500.0, 150.0, 800.0, 4500),
            mat("Teak Wood Planks", "Wood", "Cu.Ft", 30.0, 10.0, 50.0, 350000),
            mat("Sal Wood Beams", "Wood", "Cu.Ft", 20.0, 8.0, 40.0, 280000),
            mat("MDF Board 18mm", "Wood", "Sheets", 60.0, 20.0, 100.0, 120000),
            mat("Particle Board", "Wood", "Sheets", 40.0, 15.0, 70.0, 85000),
            mat("PVC Door Frames", "Wood", "Nos", 50.0, 15.0, 80.0, 250000),
            mat("Copper Wire 2.5mm", "Electrical", "Metres", 800.0, 200.0, 1500.0, 7500),
            mat("MCB 32A Single Pole", "Electrical", "Nos", 100.0, 30.0, 200.0, 25000),
            mat("Distribution Board 8-Way", "Electrical", "Nos", 20.0, 5.0, 40.0, 180000),
            mat("LED Panel Lights 18W", "Electrical", "Nos", 150.0, 50.0, 250.0, 45000),
            mat("Earthing Kit", "Electrical", "Sets", 10.0, 3.0, 20.0, 350000),
            mat("Switch Boards (Modular)", "Electrical", "Nos", 80.0, 25.0, 150.0, 55000),
            mat("PVC Pipes 4 inch (SWR)", "Plumbing", "Metres", 200.0, 60.0, 350.0, 18000),
            mat("GI Pipes 1 inch", "Plumbing", "Metres", 150.0, 40.0, 250.0, 22000),
            mat("Ball Valves 1 inch", "Plumbing", "Nos", 50.0, 15.0, 80.0, 35000),
            mat("Water Tank 1000L", "Plumbing", "Nos", 8.0, 2.0, 15.0, 650000),
            mat("Bathroom Fittings Set", "Plumbing", "Sets", 25.0, 8.0, 40.0, 450000),
            mat("RMC M20 Grade", "Concrete", "Cu.M", 30.0, 10.0, 60.0, 480000),
            mat("RMC M30 Grade", "Concrete", "Cu.M", 20.0, 8.0, 40.0, 560000),
            mat("Self Compacting Concrete", "Concrete", "Cu.M", 10.0, 5.0, 20.0, 650000),
            mat("Foam Concrete", "Concrete", "Cu.M", 15.0, 5.0, 30.0, 420000),
            mat("Curing Compound", "Chemical", "Litres", 100.0, 30.0, 180.0, 25000),
            mat("Bonding Agent (SBR)", "Chemical", "Litres", 80.0, 20.0, 150.0, 32000),
            mat("Tile Adhesive", "Chemical", "Bags", 60.0, 20.0, 100.0, 45000),
            mat("Grout (Epoxy)", "Chemical", "Kg", 40.0, 10.0, 70.0, 55000),
            mat("Construction Sealant", "Chemical", "Tubes", 100.0, 30.0, 180.0, 18000),
            mat("Anti-Termite Chemical", "Chemical", "Litres", 50.0, 15.0, 80.0, 28000),
            mat("Marble Flooring", "Stone", "Sq.Ft", 200.0, 60.0, 350.0, 25000),
            mat("Kota Stone", "Stone", "Sq.Ft", 150.0, 40.0, 250.0, 12000),
            mat("Shahabad Stone", "Stone", "Sq.Ft", 100.0, 30.0, 180.0, 8500),
            mat("Kadappa Stone", "Stone", "Sq.Ft", 80.0, 25.0, 140.0, 15000),
            mat("Aluminium Windows", "Fixtures", "Nos", 40.0, 12.0, 60.0, 850000),
            mat("UPVC Windows", "Fixtures", "Nos", 30.0, 10.0, 50.0, 650000),
            mat("Flush Doors", "Fixtures", "Nos", 50.0, 15.0, 80.0, 450000),
            mat("Panel Doors (Teak)", "Fixtures", "Nos", 20.0, 8.0, 35.0, 1200000),
            mat("Safety Door (MS)", "Fixtures", "Nos", 10.0, 3.0, 15.0, 2500000),
            mat("Glass 6mm Clear", "Fixtures", "Sq.Ft", 300.0, 80.0, 500.0, 8500),
            mat("Scaffolding Pipes", "Equipment", "Metres", 500.0, 100.0, 800.0, 12000),
            mat("Centering Plates", "Equipment", "Nos", 200.0, 50.0, 350.0, 35000),
            mat("Shuttering Plywood", "Equipment", "Sheets", 100.0, 30.0, 180.0, 150000),
            mat("Safety Helmets", "Safety", "Nos", 100.0, 30.0, 200.0, 25000),
            mat("Safety Harness", "Safety", "Nos", 30.0, 10.0, 50.0, 180000),
            mat("Safety Nets", "Safety", "Rolls", 20.0, 5.0, 35.0, 350000),
            mat("Fire Extinguisher (ABC)", "Safety", "Nos", 15.0, 5.0, 25.0, 250000),
            mat("Reflective Jackets", "Safety", "Nos", 80.0, 25.0, 150.0, 35000),
            mat("First Aid Kit", "Safety", "Nos", 10.0, 3.0, 15.0, 120000),
            mat("Rebar Coupler 16mm", "Steel", "Nos", 200.0, 50.0, 400.0, 15000),
            mat("Welding Rods (6013)", "Steel", "Packets", 50.0, 15.0, 80.0, 45000),
            mat("MS Angle 50x50x5", "Steel", "Metres", 100.0, 30.0, 180.0, 8500),
            mat("MS Flat Bar 50x6", "Steel", "Metres", 80.0, 25.0, 140.0, 6500),
            mat("Stainless Steel Railing", "Steel", "Metres", 60.0, 20.0, 100.0, 45000),
            mat("Wire Mesh 4x4", "Steel", "Sq.M", 150.0, 40.0, 250.0, 12000),
            mat("Geo Textile Fabric", "Chemical", "Sq.M", 200.0, 50.0, 350.0, 8500),
            mat("Polythene Sheet 400G", "Chemical", "Sq.M", 300.0, 80.0, 500.0, 3500),
            mat("Insulation Board (XPS)", "Chemical", "Sq.M", 100.0, 30.0, 180.0, 22000),
            mat("Expansion Joint Filler", "Chemical", "Metres", 150.0, 40.0, 250.0, 12000),
            mat("Corrugated Roofing Sheet", "Fixtures", "Nos", 80.0, 25.0, 140.0, 55000),
            mat("TMT Stirrups 8mm", "Steel", "Kg", 300.0, 80.0, 500.0, 7800),
            mat("Concrete Vibrator Needle", "Equipment", "Nos", 10.0, 3.0, 15.0, 450000),
            mat("Builder Mix (Premixed)", "Concrete", "Bags", 100.0, 30.0, 180.0, 28000)
        )
        database.materialDao().insertMaterials(materials)
    }

    private fun mat(name: String, cat: String, unit: String, stock: Double, min: Double, opening: Double, pricePaise: Long) =
        MaterialEntity(
            id = "mat_${UUID.randomUUID().toString().take(8)}",
            name = name, category = cat, unit = unit,
            currentStock = stock, minimumStock = min, openingStock = opening,
            purchasePricePaise = pricePaise, averageCostPaise = (pricePaise * 0.95).toLong(),
            remarks = null, status = if (stock <= min) "LOW_STOCK" else "IN_STOCK",
            createdAt = now - 60 * day, updatedAt = now - 2 * day
        )

    private suspend fun seedSuppliers() {
        val suppliers = listOf(
            sup("Ultratech Cement Dealer", "9811223344", "07AABCT1234R1ZH", "Cement,Concrete"),
            sup("Tata Steel Distributors", "9822334455", "27AABTT5678S1ZJ", "Steel"),
            sup("Rajasthan Sand Traders", "9833445566", null, "Sand,Stone"),
            sup("National Brick Works", "9844556677", "09AABCN9012T1ZK", "Bricks"),
            sup("Kajaria Tile Centre", "9855667788", "27AABCK3456U1ZL", "Tiles"),
            sup("Asian Paints Depot", "9866778899", "27AABCA7890V1ZM", "Paint,Chemical"),
            sup("Godrej Plywood House", "9877889900", "32AABCG1234W1ZN", "Wood"),
            sup("Havells Electrical", "9888990011", "07AABCH5678X1ZP", "Electrical"),
            sup("Supreme Pipes & Fittings", "9899001122", "27AABCS9012Y1ZQ", "Plumbing"),
            sup("ACC RMC Plant", "9900112233", "27AABCA3456Z1ZR", "Concrete,Cement")
        )
        database.supplierDao().insertSuppliers(suppliers)
    }

    private fun sup(name: String, phone: String, gst: String?, categories: String) = SupplierEntity(
        id = "sup_${UUID.randomUUID().toString().take(8)}",
        name = name, phone = phone, gst = gst, address = "Industrial Area, Sector 6",
        materialCategories = categories,
        outstandingBalancePaise = (10000 + (name.hashCode().toLong().and(0xFFFFL)) * 100) * 100,
        notes = null, createdAt = now - 90 * day, updatedAt = now - 5 * day
    )

    private suspend fun seedTransactions() {
        val categories = listOf("CLIENT_ADVANCE", "CLIENT_PAYMENT", "MATERIAL_PURCHASE",
            "LABOUR_PAYMENT", "TRANSPORT", "SITE_EXPENSE", "MISCELLANEOUS")
        val paymentMethods = listOf("CASH", "UPI", "BANK_TRANSFER", "CHEQUE")

        // 250 expense transactions + 150 income transactions = 400 total
        val expenseTransactions = (0 until 250).map { i ->
            val cat = categories[2 + (i % 5)] // MATERIAL_PURCHASE, LABOUR_PAYMENT, TRANSPORT, SITE_EXPENSE, MISCELLANEOUS
            val amount = ((3000 + i * 1500) * 100).toLong() // in paise

            TransactionEntity(
                id = "txn_exp_${UUID.randomUUID().toString().take(8)}",
                projectId = projectIds[i % projectIds.size],
                date = now - (250 - i) * day,
                time = now - (250 - i) * day + 9 * 3600000,
                type = "EXPENSE",
                category = cat,
                amountPaise = amount,
                paymentMethod = paymentMethods[i % paymentMethods.size],
                referenceNumber = if (i % 3 == 0) "REF${200000 + i}" else null,
                description = when (cat) {
                    "MATERIAL_PURCHASE" -> "Material purchase - ${listOf("Cement", "Steel", "Sand", "Bricks", "Tiles", "Paint")[i % 6]}"
                    "LABOUR_PAYMENT" -> "Weekly wages - ${workerIds[i % workerIds.size]}"
                    "TRANSPORT" -> "Material transport charges - Trip ${i + 1}"
                    "SITE_EXPENSE" -> "Site expense - ${listOf("Food", "Water", "Electricity", "Tools", "Fuel")[i % 5]}"
                    else -> "Miscellaneous expense"
                },
                createdBy = "Alex Carter",
                isDeleted = false,
                createdAt = now - (250 - i) * day,
                updatedAt = now - (250 - i) * day,
                attachmentPath = null
            )
        }

        val incomeTransactions = (0 until 150).map { i ->
            val cat = if (i % 3 == 0) "CLIENT_ADVANCE" else "CLIENT_PAYMENT"
            val amount = ((50000 + i * 25000) * 100).toLong() // in paise (larger amounts for income)

            TransactionEntity(
                id = "txn_inc_${UUID.randomUUID().toString().take(8)}",
                projectId = projectIds[i % projectIds.size],
                date = now - (200 - i) * day,
                time = now - (200 - i) * day + 11 * 3600000,
                type = "INCOME",
                category = cat,
                amountPaise = amount,
                paymentMethod = paymentMethods[i % paymentMethods.size],
                referenceNumber = "RCP${300000 + i}",
                description = when (cat) {
                    "CLIENT_ADVANCE" -> "Advance payment from client - ${clientIds[i % clientIds.size].takeLast(4)}"
                    else -> "Progress payment received - Milestone ${1 + i % 5}"
                },
                createdBy = "Alex Carter",
                isDeleted = false,
                createdAt = now - (200 - i) * day,
                updatedAt = now - (200 - i) * day,
                attachmentPath = null
            )
        }

        database.transactionDao().insertTransactions(expenseTransactions + incomeTransactions)
    }

    private suspend fun seedAttendance() {
        val statuses = listOf("PRESENT", "PRESENT", "PRESENT", "PRESENT", "ABSENT", "HALF_DAY")
        // Seed 14 days of attendance for first 30 workers (420 records > 365 required)
        val records = (0 until 14).flatMap { dayOffset ->
            val date = todayStart() - dayOffset * day
            (0 until 30).map { workerIdx ->
                AttendanceEntity(
                    id = "att_${UUID.randomUUID().toString().take(8)}",
                    workerId = workerIds[workerIdx],
                    projectId = projectIds[workerIdx % projectIds.size],
                    date = date,
                    status = statuses[(workerIdx + dayOffset) % statuses.size],
                    overtimeHours = if ((workerIdx + dayOffset) % 5 == 0) 2.0 else 0.0,
                    hoursWorked = when (statuses[(workerIdx + dayOffset) % statuses.size]) {
                        "PRESENT" -> 8.0
                        "HALF_DAY" -> 4.0
                        else -> 0.0
                    },
                    remarks = null,
                    createdAt = date + 7 * 3600000 // 7 AM
                )
            }
        }
        database.attendanceDao().insertAllAttendance(records)
    }

    private suspend fun seedPhotos() {
        val categories = listOf("Site Progress", "Before", "After", "Milestone", "Daily", "Foundation", "Structural", "Finishing")
        val photos = (0 until 150).map { i ->
            PhotoEntity(
                id = "photo_${UUID.randomUUID().toString().take(8)}",
                projectId = projectIds[i % projectIds.size],
                uri = "content://demo/project_${i % projectIds.size}/photo_$i.jpg",
                description = "Site photo ${i + 1} - ${categories[i % categories.size]} documentation",
                tags = listOf(categories[i % categories.size], "Progress", "Week ${1 + i / 7}"),
                category = categories[i % categories.size],
                date = now - (150 - i) * day,
                capturedBy = listOf("Ramesh K.", "Sunil M.", "Anil P.", "Vijay R.")[i % 4],
                location = "Site ${i % projectIds.size + 1}",
                linkedSiteDiaryId = null,
                linkedMilestoneId = null
            )
        }
        photos.forEach { database.photoDao().insertPhoto(it) }
    }

    private suspend fun seedMilestones() {
        val milestoneNames = listOf(
            "Foundation Complete", "Plinth Level", "Ground Floor Slab",
            "First Floor Slab", "Brickwork Complete", "Plastering Complete",
            "Electrical Wiring", "Plumbing Complete", "Flooring & Tiling", "Handover"
        )
        val milestones = projectIds.take(50).flatMap { projId ->
            milestoneNames.mapIndexed { idx, name ->
                MilestoneEntity(
                    id = "ms_${UUID.randomUUID().toString().take(8)}",
                    projectId = projId,
                    name = name,
                    isCompleted = idx < 3, // first 3 milestones completed for each project
                    completionDate = if (idx < 3) now - (90 - idx * 20) * day else null,
                    notes = null,
                    orderIndex = idx
                )
            }
        }
        database.projectDao().insertMilestones(milestones)
    }

    private fun todayStart(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private suspend fun seedResourceAllocations() {
        val resourceTypes = listOf("MATERIAL", "LABOUR", "EQUIPMENT")
        val allocations = (0 until 300).map { i ->
            ResourceAllocationEntity(
                id = "alloc_${UUID.randomUUID().toString().take(8)}",
                projectId = projectIds[i % projectIds.size],
                date = now - (300 - i) * day,
                resourceType = resourceTypes[i % resourceTypes.size],
                resourceId = when (i % 3) {
                    0 -> "mat_${(i % 100).toString().padStart(4, '0')}" // material ref
                    1 -> workerIds[i % workerIds.size]
                    else -> "equip_${i % 10}"
                },
                quantity = (1 + i % 20).toDouble(),
                hours = if (i % 3 == 1) 8.0 else null,
                costPaise = ((1000 + i * 500) * 100).toLong(),
                remarks = when (i % 3) {
                    0 -> "Material issued to site - ${listOf("Cement", "Steel", "Sand", "Bricks", "Tiles", "Paint")[i % 6]}"
                    1 -> "Labour allocation - ${listOf("Mason", "Carpenter", "Electrician", "Plumber")[i % 4]}"
                    else -> "Equipment usage - ${listOf("Crane", "Mixer", "Vibrator", "Excavator")[i % 4]}"
                },
                siteDiaryId = null,
                transactionId = null,
                createdAt = now - (300 - i) * day
            )
        }
        database.resourceAllocationDao().insertAllocations(allocations)
    }

    private suspend fun seedDocuments() {
        val docCategories = listOf("Invoices", "Receipts", "Contracts", "Approvals", "BOQ", "Blueprints")
        val documents = (0 until 50).map { i ->
            DocumentEntity(
                id = "doc_${UUID.randomUUID().toString().take(8)}",
                projectId = projectIds[i % projectIds.size],
                title = when (docCategories[i % docCategories.size]) {
                    "Invoices" -> "Invoice #${1000 + i} - ${listOf("Cement Supply", "Steel Delivery", "Labour Contract")[i % 3]}"
                    "Receipts" -> "Receipt #${2000 + i} - Payment ${listOf("Advance", "Progress", "Final")[i % 3]}"
                    "Contracts" -> "Contract - ${projectIds[i % projectIds.size].takeLast(4)} Work Agreement"
                    "Approvals" -> "Approval - ${listOf("Building Plan", "Structural", "Electrical", "Plumbing")[i % 4]}"
                    "BOQ" -> "BOQ - ${listOf("Civil", "Electrical", "Plumbing", "Finishing")[i % 4]} Works"
                    else -> "Blueprint - ${listOf("Ground Floor", "First Floor", "Elevation", "Section")[i % 4]}"
                },
                category = docCategories[i % docCategories.size],
                uri = "content://demo/documents/doc_$i.pdf",
                description = "Project document ${i + 1}",
                tags = listOf(docCategories[i % docCategories.size], "Project"),
                createdAt = now - (100 - i) * day,
                updatedAt = now - (50 - i % 50) * day
            )
        }
        documents.forEach { database.documentDao().insertDocument(it) }
    }
}
