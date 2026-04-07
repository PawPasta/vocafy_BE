package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.model.dto.request.PaymentMethodActiveRequest
import com.exe.vocafy_BE.model.dto.request.PaymentMethodCreateRequest
import com.exe.vocafy_BE.model.dto.request.PaymentMethodUpdateRequest
import com.exe.vocafy_BE.model.entity.PaymentMethod
import com.exe.vocafy_BE.repo.PaymentMethodRepository
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.testng.Assert.assertEquals
import org.testng.Assert.assertFalse
import org.testng.Assert.assertTrue
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.util.Optional

class PaymentMethodServiceImplTestNG {

    @Mock
    private lateinit var paymentMethodRepository: PaymentMethodRepository

    private lateinit var service: PaymentMethodServiceImpl
    private lateinit var openMocks: AutoCloseable

    @BeforeClass
    fun beforeClass() {
        openMocks = MockitoAnnotations.openMocks(this)
    }

    @BeforeMethod
    fun setUp() {
        Mockito.reset(paymentMethodRepository)
        service = PaymentMethodServiceImpl(paymentMethodRepository)
    }

    @AfterClass
    fun tearDownClass() {
        openMocks.close()
    }

    @Test(groups = ["payment-method"], description = "list returns paged payment methods")
    fun list_returnsPagedResult() {
        val pageable = PageRequest.of(0, 10)
        val entities = listOf(
            PaymentMethod(id = 1L, provider = "MOMO", description = "wallet", active = true),
            PaymentMethod(id = 2L, provider = "BANK", description = "transfer", active = false),
        )
        Mockito.`when`(paymentMethodRepository.findAll(pageable))
            .thenReturn(PageImpl(entities, pageable, entities.size.toLong()))

        val response = service.list(pageable)

        assertEquals(response.message, "Ok")
        assertEquals(response.result.content.size, 2)
        assertTrue(response.result.content.first().active)
        assertFalse(response.result.content.last().active)
    }

    @Test(
        groups = ["payment-method", "error"],
        expectedExceptions = [BaseException.BadRequestException::class],
        description = "updateActive validates active field",
    )
    fun updateActive_throwsWhenActiveIsNull() {
        service.updateActive(1L, PaymentMethodActiveRequest(active = null))
    }

    @Test(groups = ["payment-method"], description = "create persists payment method")
    fun create_returnsCreatedPaymentMethod() {
        Mockito.`when`(paymentMethodRepository.save(Mockito.any(PaymentMethod::class.java)))
            .thenAnswer { invocation ->
                val input = invocation.getArgument<PaymentMethod>(0)
                PaymentMethod(id = 99L, provider = input.provider, description = input.description, active = true)
            }

        val response = service.create(PaymentMethodCreateRequest(provider = "VNPay", description = "Gateway"))

        assertEquals(response.message, "Created")
        assertEquals(response.result.id, 99L)
        assertEquals(response.result.provider, "VNPay")
        assertTrue(response.result.active)
    }

    @Test(
        groups = ["payment-method", "error"],
        dependsOnMethods = ["create_returnsCreatedPaymentMethod"],
        expectedExceptions = [BaseException.NotFoundException::class],
        description = "update throws when payment method not found",
    )
    fun update_throwsWhenMissingEntity() {
        Mockito.`when`(paymentMethodRepository.findById(500L)).thenReturn(Optional.empty())

        service.update(500L, PaymentMethodUpdateRequest(provider = "New", description = "Updated"))
    }

    @Test(groups = ["payment-method"], description = "updateActive changes active status")
    fun updateActive_updatesStatus() {
        val existing = PaymentMethod(id = 7L, provider = "MOMO", description = "Wallet", active = true)
        Mockito.`when`(paymentMethodRepository.findById(7L)).thenReturn(Optional.of(existing))
        Mockito.`when`(paymentMethodRepository.save(Mockito.any(PaymentMethod::class.java)))
            .thenAnswer { it.getArgument(0) }

        val response = service.updateActive(7L, PaymentMethodActiveRequest(active = false))

        assertEquals(response.message, "Updated")
        assertEquals(response.result.id, 7L)
        assertFalse(response.result.active)
    }
}


