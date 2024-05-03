package ori.controller.admin;

import java.util.List;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;
import ori.entity.Category;
import ori.entity.Product;
import ori.model.CategoryModel;
import ori.service.ICategoryService;

@Controller

@RequestMapping("admin/categories")

public class CategoryController {
	@Autowired(required = true)
	ICategoryService categoryService;
	
	@RequestMapping("")
	public String list(ModelMap model, @RequestParam(name="pageNo", defaultValue = "1") Integer pageNo) {
		Page<Category> list = categoryService.getAll(pageNo);
		model.addAttribute("categories", list);
		model.addAttribute("totalPage",list.getTotalPages());
		model.addAttribute("currentPage",pageNo);
		return "admin/categories/list";
	}

	@GetMapping("add")
	public String add(ModelMap model) {
		CategoryModel cateModel = new CategoryModel();
		cateModel.setIsEdit(false);
		model.addAttribute("category", cateModel);
		return "admin/categories/addOrEdit";
	}
	
	@GetMapping("edit/{cateId}")
	public ModelAndView edit(ModelMap model, @PathVariable("cateId") Integer cateId) {
		Optional<Category> optCategory = categoryService.findById(cateId);
		CategoryModel cateModel = new CategoryModel();
		if (optCategory.isPresent()) {
			Category entity = optCategory.get();
			BeanUtils.copyProperties(entity, cateModel);
			cateModel.setIsEdit(true);
			model.addAttribute("category", cateModel);
			return new ModelAndView("admin/categories/addOrEdit", model);
		}
		model.addAttribute("message", "Category is not existed!!!!");
		return new ModelAndView("forward:/admin/categories", model);
	}

	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model, @Valid @ModelAttribute("category") CategoryModel cateMdoel, BindingResult result) {
		if (result.hasErrors()) {
			return new ModelAndView("admin/categories/addOrEdit");
		}
		Category entity = new Category();
		//copy từ Model sang Entity
		BeanUtils.copyProperties(cateMdoel, entity);
		//gọi hàm save trong service
		categoryService.save(entity);
		//đưa thông báo về cho biến message
		String message = "";
		if (cateMdoel.getIsEdit() == true) {
			message = "Category is Edited!!!!!!!!";
		} else {
			message = "Category is saved!!!!!!!!";
		}
		model.addAttribute("message", message);
		//redirect về URL controller
		return new ModelAndView("forward:/admin/categories", model);
	}

	@GetMapping("delete/{categoryId}")
	public ModelAndView delet(ModelMap model, @PathVariable("categoryId") Integer categoryId) {
		try {
			categoryService.deleteById(categoryId);
			model.addAttribute("message", "Category is deleted!!!!");
		} catch (Exception e) {
			model.addAttribute("message", "Cannot delete!!!!");
		}
		return new ModelAndView("forward:/admin/categories", model);
	}
//	@GetMapping("search")
	//
//	public String search(ModelMap model, @RequestParam(name = "name", required = false) String name) {
//
//		List<Category> list = null;
//
////có nội dung truyền về không, name là tùy chọn khi required=false
//
//		if (StringUtils.hasText(name)) {
//
//			list = categoryService.findByCategoryNameContaining(name);
//
//		} else {
//
//			list = categoryService.findAll();
//
//		}
//
//		model.addAttribute("categories", list);
//
//		return "admin/categories/search";
//
//	}
//
//	@RequestMapping("searchpaginated")
//
//	public String search(ModelMap model,
//
//			@RequestParam(name = "categoryName", required = false) String name,
//
//			@RequestParam("page") Optional<Integer> page,
//
//			@RequestParam("size") Optional<Integer> size) {
//
//		int count = (int) categoryService.count();
//
//		int currentPage = page.orElse(1);
//
//		int pageSize = size.orElse(3);
//
//		Pageable pageable = PageRequest.of(currentPage - 1, pageSize, Sort.by("categoryName"));
//
//		Page<Category> resultPage = null;
//
//		if (StringUtils.hasText(name)) {
//
//			resultPage = categoryService.findByCategoryNameContaining(name, pageable);
//
//			model.addAttribute("name", name);
//
//		} else {
//
//			resultPage = categoryService.findAll(pageable);
//
//		}
//
//		int totalPages = resultPage.getTotalPages();
//
//		if (totalPages > 0) {
//
//			int start = Math.max(1, currentPage - 2);
//
//			int end = Math.min(currentPage + 2, totalPages);
//
//			if (totalPages > count) {
//
//				if (end == totalPages)
//					start = end - count;
//
//				else if (start == 1)
//					end = start + count;
//
//			}
//
//			List<Integer> pageNumbers = IntStream.rangeClosed(start, end)
//
//					.boxed()
//
//					.collect(Collectors.toList());
//
//			model.addAttribute("pageNumbers", pageNumbers);
//
//		}
//
//		model.addAttribute("categoryPage", resultPage);
//
//		return "admin/categories/searchpaginated";
//
//	}
}
