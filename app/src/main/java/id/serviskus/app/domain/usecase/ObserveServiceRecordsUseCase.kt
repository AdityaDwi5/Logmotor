package id.serviskus.app.domain.usecase

import id.serviskus.app.domain.model.ServiceRecord
import id.serviskus.app.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveServiceRecordsUseCase @Inject constructor(
    private val serviceRepository: ServiceRepository,
) {
    operator fun invoke(vehicleId: Long): Flow<List<ServiceRecord>> =
        serviceRepository.observeServiceRecords(vehicleId)
}
