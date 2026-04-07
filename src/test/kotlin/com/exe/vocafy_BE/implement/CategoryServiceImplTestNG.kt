package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.model.dto.request.CategoryCreateRequest
import com.exe.vocafy_BE.model.dto.request.CategoryUpdateRequest
import com.exe.vocafy_BE.model.entity.Category
import com.exe.vocafy_BE.repo.CategoryRepository
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.testng.Assert.assertEquals
import org.testng.Assert.assertNotNull
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeMethod
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.util.Optional

class CategoryServiceImplTestNG {

    @Mock
    private lateinit var categoryRepository: CategoryRepository

    private lateinit var service: CategoryServiceImpl
    private lateinit var openMocks: AutoCloseable

    @BeforeClass
    fun beforeClass() {
        openMocks = MockitoAnnotations.openMocks(this)
    }

    @BeforeMethod
    fun resetMocks() {
        Mockito.reset(categoryRepository)
        service = CategoryServiceImpl(categoryRepository)
    }

    @AfterClass
    fun afterClass() {
        openMocks.close()
    }

    @Test(groups = ["category"], description = "Create category returns mapped response")
    fun create_returnsCreatedCategory() {
        Mockito.`when`(categoryRepository.save(Mockito.any(Category::class.java)))
            .thenAnswer { invocation ->
                val input = invocation.getArgument<Category>(0)
                Category(id = 1L, name = input.name, description = input.description)
            }

        val result = service.create(CategoryCreateRequest(name = "N5", description = "Basic"))

        assertEquals(result.message, "Created")
        assertEquals(result.result.id, 1L)
        assertEquals(result.result.name, "N5")
        assertEquals(result.result.description, "Basic")
        Mockito.verify(categoryRepository, times(1)).save(Mockito.any(Category::class.java))
    }

    @Test(
        groups = ["category", "error"],
        expectedExceptions = [BaseException.NotFoundException::class],
        description = "getById throws NotFound when category does not exist",
    )
    fun getById_throwsWhenNotFound() {
        Mockito.`when`(categoryRepository.findById(404L)).thenReturn(Optional.empty())

        service.getById(404L)
    }

    @DataProvider(name = "nameFilterProvider")
    fun nameFilterProvider(): Array<Array<Any?>> = arrayOf(
        arrayOf(null, 2L),
        arrayOf("ja", 1L),
    )

    @Test(dataProvider = "nameFilterProvider", groups = ["category"])
    fun list_selectsRightRepositoryMethod(name: String?, expectedTotal: Long) {
        val pageable = PageRequest.of(0, 10)
        val allItems = listOf(Category(id = 1L, name = "N5"), Category(id = 2L, name = "N4"))
        val filteredItems = listOf(Category(id = 1L, name = "Japanese"))

        if (name.isNullOrBlank()) {
            Mockito.`when`(categoryRepository.findAll(pageable))
                .thenReturn(PageImpl(allItems, pageable, allItems.size.toLong()))
        } else {
            Mockito.`when`(categoryRepository.findByNameContainingIgnoreCase(name, pageable))
                .thenReturn(PageImpl(filteredItems, pageable, filteredItems.size.toLong()))
        }

        val response = service.list(name, pageable)

        assertEquals(response.message, "Ok")
        assertNotNull(response.result)
        assertEquals(response.result.totalElements, expectedTotal)

        if (name.isNullOrBlank()) {
            Mockito.verify(categoryRepository, times(1)).findAll(pageable)
            Mockito.verify(categoryRepository, never()).findByNameContainingIgnoreCase("ja", pageable)
        } else {
            Mockito.verify(categoryRepository, times(1)).findByNameContainingIgnoreCase(name, pageable)
            Mockito.verify(categoryRepository, never()).findAll(pageable)
        }
    }

    @Test(
        groups = ["category", "error"],
        dependsOnMethods = ["create_returnsCreatedCategory"],
        expectedExceptions = [BaseException.NotFoundException::class],
        description = "delete throws NotFound for unknown id",
    )
    fun delete_throwsWhenMissing() {
        Mockito.`when`(categoryRepository.existsById(999L)).thenReturn(false)

        service.delete(999L)
    }

    @Test(groups = ["category"], description = "update applies new values and returns updated response")
    fun update_updatesExistingCategory() {
        val existing = Category(id = 10L, name = "Old", description = "Old desc")
        Mockito.`when`(categoryRepository.findById(10L)).thenReturn(Optional.of(existing))
        Mockito.`when`(categoryRepository.save(Mockito.any(Category::class.java)))
            .thenAnswer { it.getArgument(0) }

        val response = service.update(10L, CategoryUpdateRequest(name = "New", description = "New desc"))

        assertEquals(response.message, "Updated")
        assertEquals(response.result.id, 10L)
        assertEquals(response.result.name, "New")
        assertEquals(response.result.description, "New desc")
    }
}


