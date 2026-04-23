Project update summary for backend implementation based on database-setup.sql.

Implemented user endpoints:
GET /api/v1/user/requests to fetch current user requests.
GET /api/v1/user/requests/{referenceCode}/files to fetch files under a request reference code with ownership checking.
GET /api/v1/user/files/{fileRecordId}/download to return a pre-signed download URL, allowed for file owner or admin roles.
POST /api/v1/user/requests/contractor to create contractor requests with optional source reference code and multiple attachments.
GET /api/v1/user/transactions to fetch current user transaction history.
GET /api/v1/user/credits to fetch current user credits information.

Implemented admin endpoints:
POST /api/admin/requests/search to search all user requests with pagination, keyword, and status filters.
PATCH /api/admin/requests/{requestId} to update request status and admin reply in resultInfo.
POST /api/admin/announcements to create announcement.
GET /api/admin/announcements to list announcements.
GET /api/admin/announcements/{id} to fetch one announcement.
PUT /api/admin/announcements/{id} to update announcement.
DELETE /api/admin/announcements/{id} to delete announcement.
POST /api/admin/transactions/search to search all transactions with pagination and filters.
POST /api/admin/credits/search to search all user credits with pagination and filters.
POST /api/admin/credits/adjust to adjust user credits and create a matching transaction record.

Best practice search refactor:
Replaced JPQL search methods with JPA Specification in service search flows.
Repository search now uses JpaSpecificationExecutor for UserRequest, Transaction, and UserCredits.
Added dedicated specification classes for composable and maintainable filtering logic.
Search filters now normalize status and transaction type values, and support numeric keyword matching for userId.

Quality and safety updates:
Added validation and exception handling for not found resources and invalid request payload.
Added comments in controller and service methods to explain business flow and permission checks.
Ensured new user portal routes are protected by authentication in SecurityConfig.