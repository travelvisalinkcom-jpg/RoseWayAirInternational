package com.example.data.supabase

import com.example.data.model.Lead
import com.example.data.model.StaffMember

object LeadAssignmentEngine {

    const val OFFICIAL_FALLBACK_WHATSAPP = "+966561226349"
    const val SLA_DURATION_MS = 5 * 60 * 1000L // 5-minute SLA

    /**
     * Finds the next eligible Rose Way staff member for incoming public lead assignment.
     * Criteria:
     * - isActive = true
     * - deleted = false
     * - leadEligible = true
     * - receivingPaused = false
     * Prioritizes lowest assigned or fair round-robin.
     */
    fun assignLeadToStaff(
        leadName: String,
        phone: String,
        whatsapp: String,
        passportNo: String,
        country: String,
        serviceType: String,
        notes: String,
        source: String = "Public Apply Form",
        availableStaff: List<StaffMember>
    ): Pair<Lead, StaffMember?> {
        val eligibleStaff = availableStaff.filter {
            it.isActive && !it.deleted && it.leadEligible && !it.receivingPaused
        }

        val assignedStaff = if (eligibleStaff.isNotEmpty()) {
            // Sort by totalAssigned ascending, then by lastAssignedAt (oldest first)
            eligibleStaff.minByOrNull { staff ->
                val lastTime = staff.lastAssignedAt ?: 0L
                (staff.totalAssigned * 1000000L) + (lastTime / 1000L)
            }
        } else {
            null
        }

        val staffRef = assignedStaff?.staffId ?: "S001"
        val staffName = assignedStaff?.name ?: "Rose Way Central Desk"
        val now = System.currentTimeMillis()

        val lead = Lead(
            id = "LEAD-${System.currentTimeMillis() % 1000000}",
            organizationId = "11111111-1111-4111-8111-111111111111",
            name = leadName,
            phone = phone,
            whatsapp = if (whatsapp.isNotBlank()) whatsapp else phone,
            passportNo = passportNo,
            destinationCountry = country,
            serviceType = serviceType,
            staffReference = staffRef,
            staffName = staffName,
            assignedAt = now,
            assignmentDeadlineAt = now + SLA_DURATION_MS,
            assignmentStatus = "PENDING",
            notes = notes,
            source = source,
            createdAt = now
        )

        return Pair(lead, assignedStaff)
    }

    fun getWhatsAppContactNumber(staff: StaffMember?): String {
        return if (staff != null && staff.phone.isNotBlank()) {
            staff.phone.replace(" ", "").replace("-", "")
        } else {
            OFFICIAL_FALLBACK_WHATSAPP
        }
    }
}
