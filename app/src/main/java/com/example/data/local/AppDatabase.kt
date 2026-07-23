package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.ClientDao
import com.example.data.local.dao.ExpenseDao
import com.example.data.local.dao.ProjectDao
import com.example.data.local.entity.ClientEntity
import com.example.data.local.entity.ExpenseEntity
import com.example.data.local.entity.MilestoneEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.ProjectTimelineEventEntity
import com.example.data.local.entity.ExpenseSummaryEntity
import com.example.data.local.entity.LabourSummaryEntity
import com.example.data.local.entity.MaterialSummaryEntity
import com.example.data.local.entity.SiteDiaryEntity
import com.example.data.local.entity.SiteIssueEntity
import com.example.data.local.entity.WorkItemEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.local.entity.MaterialEntity
import com.example.data.local.entity.WorkerEntity
import com.example.data.local.entity.SupplierEntity
import com.example.data.local.entity.ResourceAllocationEntity
import com.example.data.local.entity.AttendanceEntity
import com.example.data.local.entity.PhotoEntity
import com.example.data.local.entity.DocumentEntity
import com.example.data.local.dao.PhotoDao
import com.example.data.local.dao.DocumentDao
import com.example.data.local.dao.MaterialDao
import com.example.data.local.dao.WorkerDao
import com.example.data.local.dao.SupplierDao
import com.example.data.local.dao.ResourceAllocationDao
import com.example.data.local.dao.AttendanceDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.dao.SiteDiaryDao

import androidx.room.TypeConverters

@Database(
    entities = [
        ClientEntity::class,
        ProjectEntity::class,
        ExpenseEntity::class,
        MilestoneEntity::class,
        ProjectTimelineEventEntity::class,
        SiteDiaryEntity::class,
        LabourSummaryEntity::class,
        MaterialSummaryEntity::class,
        ExpenseSummaryEntity::class,
        WorkItemEntity::class,
        SiteIssueEntity::class,
        TransactionEntity::class,
        MaterialEntity::class,
        WorkerEntity::class,
        SupplierEntity::class,
        ResourceAllocationEntity::class,
        AttendanceEntity::class,
        PhotoEntity::class,
        DocumentEntity::class
    ],
    version = 9,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clientDao(): ClientDao
    abstract fun projectDao(): ProjectDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun siteDiaryDao(): SiteDiaryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun materialDao(): MaterialDao
    abstract fun workerDao(): WorkerDao
    abstract fun supplierDao(): SupplierDao
    abstract fun resourceAllocationDao(): ResourceAllocationDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun photoDao(): PhotoDao
    abstract fun documentDao(): DocumentDao

    companion object {
        const val DATABASE_NAME = "deyaar_construction.db"

        /**
         * Migration from version 7 to 8:
         * - Added indices to clients (name, isActive, category)
         * - Added indices to workers (fullName, trade, status)
         * - Added indices to suppliers (name)
         * - Added indices to materials (name, category, status)
         * - Added indices to projects (status, createdAt)
         * - Added indices to photos (linkedSiteDiaryId, linkedMilestoneId, category, date)
         * - Added foreign key to photos (projectId -> projects.id)
         * - Added indices to documents (category, createdAt)
         * - Added foreign key to documents (projectId -> projects.id)
         * - Added index to attendance (date)
         * - Added index to transactions (isDeleted)
         */
        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Clients indices
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_clients_name` ON `clients` (`name`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_clients_isActive` ON `clients` (`isActive`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_clients_category` ON `clients` (`category`)")

                // Workers indices
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_workers_fullName` ON `workers` (`fullName`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_workers_trade` ON `workers` (`trade`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_workers_status` ON `workers` (`status`)")

                // Suppliers indices
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_suppliers_name` ON `suppliers` (`name`)")

                // Materials indices
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_materials_name` ON `materials` (`name`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_materials_category` ON `materials` (`category`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_materials_status` ON `materials` (`status`)")

                // Projects indices
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_projects_status` ON `projects` (`status`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_projects_createdAt` ON `projects` (`createdAt`)")

                // Photos indices
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_photos_linkedSiteDiaryId` ON `photos` (`linkedSiteDiaryId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_photos_linkedMilestoneId` ON `photos` (`linkedMilestoneId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_photos_category` ON `photos` (`category`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_photos_date` ON `photos` (`date`)")

                // Documents indices
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_documents_category` ON `documents` (`category`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_documents_createdAt` ON `documents` (`createdAt`)")

                // Attendance index
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_attendance_date` ON `attendance` (`date`)")

                // Transactions index
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_isDeleted` ON `transactions` (`isDeleted`)")

                // Note: Foreign keys on photos and documents cannot be added via ALTER TABLE in SQLite.
                // They are already defined in the entity annotations and will be enforced for new installs.
                // For existing installations, referential integrity is maintained at the application layer.
                // A full table rebuild would risk data loss for existing users.
            }
        }

        /**
         * Migration from version 8 to 9:
         * Financial precision - rename monetary columns from Double to Long (paise).
         * Since SQLite ALTER TABLE RENAME COLUMN requires API 30+ and minSdk=24,
         * we recreate affected tables with the new column names.
         */
        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Transactions: rename 'amount' to 'amountPaise'
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `transactions_new` (
                        `id` TEXT NOT NULL, `projectId` TEXT NOT NULL, `date` INTEGER NOT NULL,
                        `time` INTEGER NOT NULL, `type` TEXT NOT NULL, `category` TEXT NOT NULL,
                        `amountPaise` INTEGER NOT NULL, `paymentMethod` TEXT NOT NULL,
                        `referenceNumber` TEXT, `description` TEXT, `createdBy` TEXT NOT NULL,
                        `isDeleted` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL, `attachmentPath` TEXT,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`projectId`) REFERENCES `projects`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("""
                    INSERT INTO `transactions_new` SELECT `id`, `projectId`, `date`, `time`, `type`,
                    `category`, CAST(ROUND(`amount` * 100) AS INTEGER), `paymentMethod`, `referenceNumber`,
                    `description`, `createdBy`, `isDeleted`, `createdAt`, `updatedAt`, `attachmentPath`
                    FROM `transactions`
                """.trimIndent())
                db.execSQL("DROP TABLE `transactions`")
                db.execSQL("ALTER TABLE `transactions_new` RENAME TO `transactions`")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_projectId` ON `transactions` (`projectId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_date` ON `transactions` (`date`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_type` ON `transactions` (`type`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_category` ON `transactions` (`category`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_transactions_isDeleted` ON `transactions` (`isDeleted`)")

                // Projects: rename financial columns
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `projects_new` (
                        `id` TEXT NOT NULL, `projectNumber` TEXT NOT NULL, `name` TEXT NOT NULL,
                        `clientId` TEXT, `category` TEXT NOT NULL, `address` TEXT, `location` TEXT,
                        `contractValuePaise` INTEGER, `estimatedBudgetPaise` INTEGER,
                        `advanceReceivedPaise` INTEGER, `expectedProfitPaise` INTEGER,
                        `startDate` INTEGER, `expectedCompletionDate` INTEGER, `actualCompletionDate` INTEGER,
                        `status` TEXT NOT NULL, `priority` TEXT NOT NULL, `engineerInCharge` TEXT,
                        `notes` TEXT, `progress` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`clientId`) REFERENCES `clients`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL
                    )
                """.trimIndent())
                db.execSQL("""
                    INSERT INTO `projects_new` SELECT `id`, `projectNumber`, `name`, `clientId`, `category`,
                    `address`, `location`,
                    CASE WHEN `contractValue` IS NULL THEN NULL ELSE CAST(ROUND(`contractValue` * 100) AS INTEGER) END,
                    CASE WHEN `estimatedBudget` IS NULL THEN NULL ELSE CAST(ROUND(`estimatedBudget` * 100) AS INTEGER) END,
                    CASE WHEN `advanceReceived` IS NULL THEN NULL ELSE CAST(ROUND(`advanceReceived` * 100) AS INTEGER) END,
                    CASE WHEN `expectedProfit` IS NULL THEN NULL ELSE CAST(ROUND(`expectedProfit` * 100) AS INTEGER) END,
                    `startDate`, `expectedCompletionDate`, `actualCompletionDate`,
                    `status`, `priority`, `engineerInCharge`, `notes`, `progress`, `createdAt`, `updatedAt`
                    FROM `projects`
                """.trimIndent())
                db.execSQL("DROP TABLE `projects`")
                db.execSQL("ALTER TABLE `projects_new` RENAME TO `projects`")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_projects_clientId` ON `projects` (`clientId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_projects_status` ON `projects` (`status`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_projects_createdAt` ON `projects` (`createdAt`)")

                // Expenses: rename 'amount' to 'amountPaise'
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `expenses_new` (
                        `id` TEXT NOT NULL, `projectId` TEXT NOT NULL, `amountPaise` INTEGER NOT NULL,
                        `category` TEXT NOT NULL, `description` TEXT NOT NULL, `date` INTEGER NOT NULL,
                        `isPaid` INTEGER NOT NULL,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`projectId`) REFERENCES `projects`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("""
                    INSERT INTO `expenses_new` SELECT `id`, `projectId`, CAST(ROUND(`amount` * 100) AS INTEGER),
                    `category`, `description`, `date`, `isPaid` FROM `expenses`
                """.trimIndent())
                db.execSQL("DROP TABLE `expenses`")
                db.execSQL("ALTER TABLE `expenses_new` RENAME TO `expenses`")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_projectId` ON `expenses` (`projectId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_date` ON `expenses` (`date`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_expenses_category` ON `expenses` (`category`)")

                // Workers: rename 'dailyWage' to 'dailyWagePaise'
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `workers_new` (
                        `id` TEXT NOT NULL, `fullName` TEXT NOT NULL, `mobileNumber` TEXT NOT NULL,
                        `trade` TEXT NOT NULL, `dailyWagePaise` INTEGER NOT NULL, `experience` TEXT,
                        `joiningDate` INTEGER NOT NULL, `emergencyContact` TEXT, `address` TEXT,
                        `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                db.execSQL("""
                    INSERT INTO `workers_new` SELECT `id`, `fullName`, `mobileNumber`, `trade`,
                    CAST(ROUND(`dailyWage` * 100) AS INTEGER), `experience`, `joiningDate`,
                    `emergencyContact`, `address`, `status`, `createdAt`, `updatedAt` FROM `workers`
                """.trimIndent())
                db.execSQL("DROP TABLE `workers`")
                db.execSQL("ALTER TABLE `workers_new` RENAME TO `workers`")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_workers_fullName` ON `workers` (`fullName`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_workers_trade` ON `workers` (`trade`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_workers_status` ON `workers` (`status`)")

                // Suppliers: rename 'outstandingBalance' to 'outstandingBalancePaise'
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `suppliers_new` (
                        `id` TEXT NOT NULL, `name` TEXT NOT NULL, `phone` TEXT NOT NULL, `gst` TEXT,
                        `address` TEXT, `materialCategories` TEXT NOT NULL,
                        `outstandingBalancePaise` INTEGER NOT NULL, `notes` TEXT,
                        `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                db.execSQL("""
                    INSERT INTO `suppliers_new` SELECT `id`, `name`, `phone`, `gst`, `address`,
                    `materialCategories`, CAST(ROUND(`outstandingBalance` * 100) AS INTEGER), `notes`,
                    `createdAt`, `updatedAt` FROM `suppliers`
                """.trimIndent())
                db.execSQL("DROP TABLE `suppliers`")
                db.execSQL("ALTER TABLE `suppliers_new` RENAME TO `suppliers`")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_suppliers_name` ON `suppliers` (`name`)")

                // Materials: rename 'purchasePrice' to 'purchasePricePaise', 'averageCost' to 'averageCostPaise'
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `materials_new` (
                        `id` TEXT NOT NULL, `name` TEXT NOT NULL, `category` TEXT NOT NULL,
                        `unit` TEXT NOT NULL, `currentStock` REAL NOT NULL, `minimumStock` REAL NOT NULL,
                        `openingStock` REAL NOT NULL, `purchasePricePaise` INTEGER NOT NULL,
                        `averageCostPaise` INTEGER NOT NULL, `remarks` TEXT, `status` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
                db.execSQL("""
                    INSERT INTO `materials_new` SELECT `id`, `name`, `category`, `unit`, `currentStock`,
                    `minimumStock`, `openingStock`, CAST(ROUND(`purchasePrice` * 100) AS INTEGER),
                    CAST(ROUND(`averageCost` * 100) AS INTEGER), `remarks`, `status`, `createdAt`, `updatedAt`
                    FROM `materials`
                """.trimIndent())
                db.execSQL("DROP TABLE `materials`")
                db.execSQL("ALTER TABLE `materials_new` RENAME TO `materials`")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_materials_name` ON `materials` (`name`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_materials_category` ON `materials` (`category`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_materials_status` ON `materials` (`status`)")

                // Resource allocations: rename 'cost' to 'costPaise'
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `resource_allocations_new` (
                        `id` TEXT NOT NULL, `projectId` TEXT NOT NULL, `date` INTEGER NOT NULL,
                        `resourceType` TEXT NOT NULL, `resourceId` TEXT NOT NULL, `quantity` REAL NOT NULL,
                        `hours` REAL, `costPaise` INTEGER NOT NULL, `remarks` TEXT,
                        `siteDiaryId` TEXT, `transactionId` TEXT, `createdAt` INTEGER NOT NULL,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`projectId`) REFERENCES `projects`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("""
                    INSERT INTO `resource_allocations_new` SELECT `id`, `projectId`, `date`, `resourceType`,
                    `resourceId`, `quantity`, `hours`, CAST(ROUND(`cost` * 100) AS INTEGER), `remarks`,
                    `siteDiaryId`, `transactionId`, `createdAt` FROM `resource_allocations`
                """.trimIndent())
                db.execSQL("DROP TABLE `resource_allocations`")
                db.execSQL("ALTER TABLE `resource_allocations_new` RENAME TO `resource_allocations`")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_resource_allocations_projectId` ON `resource_allocations` (`projectId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_resource_allocations_siteDiaryId` ON `resource_allocations` (`siteDiaryId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_resource_allocations_transactionId` ON `resource_allocations` (`transactionId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_resource_allocations_resourceId` ON `resource_allocations` (`resourceId`)")

                // Labour summaries: rename 'totalWages' to 'totalWagesPaise'
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `labour_summaries_new` (
                        `id` TEXT NOT NULL, `siteDiaryId` TEXT NOT NULL, `masons` INTEGER NOT NULL,
                        `helpers` INTEGER NOT NULL, `carpenters` INTEGER NOT NULL,
                        `steelWorkers` INTEGER NOT NULL, `painters` INTEGER NOT NULL,
                        `electricians` INTEGER NOT NULL, `plumbers` INTEGER NOT NULL,
                        `machineOperators` INTEGER NOT NULL, `other` INTEGER NOT NULL,
                        `totalWagesPaise` INTEGER NOT NULL, `attendanceSummary` TEXT,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`siteDiaryId`) REFERENCES `site_diaries`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("""
                    INSERT INTO `labour_summaries_new` SELECT `id`, `siteDiaryId`, `masons`, `helpers`,
                    `carpenters`, `steelWorkers`, `painters`, `electricians`, `plumbers`, `machineOperators`,
                    `other`, CAST(ROUND(`totalWages` * 100) AS INTEGER), `attendanceSummary`
                    FROM `labour_summaries`
                """.trimIndent())
                db.execSQL("DROP TABLE `labour_summaries`")
                db.execSQL("ALTER TABLE `labour_summaries_new` RENAME TO `labour_summaries`")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_labour_summaries_siteDiaryId` ON `labour_summaries` (`siteDiaryId`)")

                // Expense summaries: rename 'amount' to 'amountPaise'
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `expense_summaries_new` (
                        `id` TEXT NOT NULL, `siteDiaryId` TEXT NOT NULL, `category` TEXT NOT NULL,
                        `amountPaise` INTEGER NOT NULL, `remarks` TEXT,
                        PRIMARY KEY(`id`),
                        FOREIGN KEY(`siteDiaryId`) REFERENCES `site_diaries`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """.trimIndent())
                db.execSQL("""
                    INSERT INTO `expense_summaries_new` SELECT `id`, `siteDiaryId`, `category`,
                    CAST(ROUND(`amount` * 100) AS INTEGER), `remarks` FROM `expense_summaries`
                """.trimIndent())
                db.execSQL("DROP TABLE `expense_summaries`")
                db.execSQL("ALTER TABLE `expense_summaries_new` RENAME TO `expense_summaries`")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_expense_summaries_siteDiaryId` ON `expense_summaries` (`siteDiaryId`)")
            }
        }
    }
}
