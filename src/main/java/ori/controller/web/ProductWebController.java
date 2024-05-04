package ori.controller.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletResponse;
import ori.config.scurity.AuthUser;
import ori.entity.Brand;
import ori.entity.Cart;
import ori.entity.CartKey;
import ori.entity.Category;
import ori.entity.OrderDetail;
import ori.entity.Product;
import ori.entity.ShoppingSession;
import ori.entity.ShoppingSessionKey;
import ori.entity.User;
import ori.model.ProductModel;
import ori.model.UserModel;
import ori.service.IBrandService;
import ori.service.ICartService;
import ori.service.ICategoryService;
import ori.service.IOrderDetailService;
import ori.service.IProductService;
import ori.service.IShoppingSessionService;
import ori.service.IUserService;
import ori.service.ShoppingSessionServiceImpl;

@Controller
@RequestMapping("web/product")
public class ProductWebController {
	@Autowired(required = true)
	IProductService proService;
	@Autowired(required = true)
	IShoppingSessionService ssService;
	@Autowired(required=true)
	IUserService userService;
	@Autowired(required=true)
	ICartService cartService;
	@Autowired(required = true)
	ICategoryService categoryService;
	@Autowired(required = true)
	IOrderDetailService orderDetailService;
	@Autowired(required = true)
	IBrandService brandService;
	@GetMapping("/search")
	public String searchPro(
			ModelMap model, 
			@RequestParam(name = "orderby", defaultValue = "") String orderby,
			@RequestParam(name = "searchPro", defaultValue = "") String search,
			@RequestParam(name = "page", defaultValue = "") Integer pageNo,
			@RequestParam(name="min_price", defaultValue = "") String min_price,
            @RequestParam(name="max_price", defaultValue = "") String max_price) {
		int pageSize = 21;
		int totalProducts = proService.searchProductByName(search).size(); // Số lượng sản phẩm tổng cộng trong cơ sở dữ liệu
		int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
		
	    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
		Page<Product> productPage  = proService.searchProductByName(search,pageable);
		List<Product> listPro = new ArrayList<>(productPage.getContent()); 
		if (Integer.parseInt(min_price) == 0 && Integer.parseInt(max_price) == 0) {
			model.addAttribute("countPro", totalProducts);	
			
			if (orderby.equals("menu_order")) {
				model.addAttribute("listAllProduct", listPro);	
			}
			else if (orderby.equals("selling")) {
				Page<Product> sortBySelling = proService.sortSearchByStock(search,pageable);
				List<Product> listProSort = new ArrayList<>(sortBySelling.getContent()); 
				model.addAttribute("listAllProduct", listProSort);
			}
			else if (orderby.equals("date")) {
				Page<Product> sortBySelling = proService.sortSearchByDate(search,pageable);
				List<Product> listProSort = new ArrayList<>(sortBySelling.getContent()); 
				model.addAttribute("listAllProduct", listProSort);
			}
			else if (orderby.equals("price")) {
				Page<Product> sortBySelling = proService.sortSearchByPrice(search,pageable);
				List<Product> listProSort = new ArrayList<>(sortBySelling.getContent()); 
				model.addAttribute("listAllProduct", listProSort);
			}
			else if (orderby.equals("price-desc")) {
				Page<Product> sortBySelling = proService.sortSearchByPriceDesc(search,pageable);
				List<Product> listProSort = new ArrayList<>(sortBySelling.getContent()); 
				model.addAttribute("listAllProduct", listProSort);
			}
			model.addAttribute("currentPage", pageNo);

			double minPriceSale = listPro.stream()
			        .mapToDouble(product -> product.getPrice() * (100 - product.getSale()) / 100)
			        .min()
			        .orElse(0);
			// Tính giá bán tối đa sau khi giảm giá
			double maxPriceSale = listPro.stream()
			        .mapToDouble(product -> product.getPrice() * (100 - product.getSale()) / 100)
			        .max()
			        .orElse(0);

			int minPriceSaleInt = (int) Math.round(minPriceSale);
			int maxPriceSaleInt = (int) Math.round(maxPriceSale);
			model.addAttribute("min_price", minPriceSaleInt);
			model.addAttribute("max_price", maxPriceSaleInt);
			model.addAttribute("min_form", minPriceSaleInt);
			model.addAttribute("max_form", maxPriceSaleInt);
		}
		else {
			totalProducts = proService.findSearchProductsByPriceRange(search,min_price,max_price).size(); // Số lượng sản phẩm tổng cộng trong cơ sở dữ liệu
				
			if (orderby.equals("menu_order")) {
				model.addAttribute("listAllProduct", proService.searchAllProductByName(search,min_price,max_price, pageable));
			}			
			else if (orderby.equals("selling")) {
				Page<Product> sortBySelling = proService.sortSearchByStockRangePrice(search,min_price,max_price,pageable);
				List<Product> listProSort = new ArrayList<>(sortBySelling.getContent()); 
				model.addAttribute("listAllProduct", listProSort);
			}
			else if (orderby.equals("date")) {
				Page<Product> sortByDate = proService.sortSearchByDateRangePrice(search,min_price,max_price,pageable);
				List<Product> listProSort = new ArrayList<>(sortByDate.getContent()); 
				model.addAttribute("listAllProduct", listProSort);
			}
			else if (orderby.equals("price")) {
				Page<Product> sortByPrice = proService.sortSearchByPriceRangePrice(search,min_price,max_price,pageable);
				List<Product> listProSort = new ArrayList<>(sortByPrice.getContent()); 
				model.addAttribute("listAllProduct", listProSort);
			}
			else if (orderby.equals("price-desc")) {
				Page<Product> sortByPriceDesc = proService.sortSearchByPriceDescRangePrice(search,min_price,max_price,pageable);
				List<Product> listProSort = new ArrayList<>(sortByPriceDesc.getContent()); 
				model.addAttribute("listAllProduct", listProSort);
			}
			model.addAttribute("countPro", totalProducts);
			model.addAttribute("currentPage", pageNo);

			double minPriceSale = listPro.stream()
			        .mapToDouble(product -> product.getPrice() * (100 - product.getSale()) / 100)
			        .min()
			        .orElse(0);

			// Tính giá bán tối đa sau khi giảm giá
			double maxPriceSale = listPro.stream()
			        .mapToDouble(product -> product.getPrice() * (100 - product.getSale()) / 100)
			        .max()
			        .orElse(0);

			int minPriceSaleInt = (int) Math.round(minPriceSale);
			int maxPriceSaleInt = (int) Math.round(maxPriceSale);
			model.addAttribute("min_price", minPriceSaleInt);
			model.addAttribute("max_price", maxPriceSaleInt);
			model.addAttribute("min_form", min_price);
			model.addAttribute("max_form", max_price);
		}
		model.addAttribute("orderby", orderby);
		model.addAttribute("search_text", search);
		totalPages = (int) Math.ceil((double) totalProducts / pageSize);	
		
		int startPage, endPage;
	    if (totalPages <= 5) {
	        startPage = 1;
	        if (totalPages > 0)
	        	endPage = totalPages;
	        else {
				endPage = 1;
				totalPages = 1;
			}
	    } else {
	        if (pageNo <= 3) {
	            startPage = 1;
	            endPage = 5;
	        } else if (pageNo + 1 >= totalPages) {
	            startPage = totalPages - 4;
	            endPage = totalPages;
	        } else {
	            startPage = pageNo - 2;
	            endPage = pageNo + 2;
	        }
	    }

	    model.addAttribute("startPage", startPage);
	    model.addAttribute("endPage", endPage);
	    model.addAttribute("lastPage", totalPages);
	    if (pageNo > totalPages) {
		    pageNo = totalPages; // Đặt pageNo bằng totalPages nếu vượt quá số trang thực tế
		}
		return "web/search-product";
	}
	@GetMapping("/{cateID}/page/{pageNo}")
	public String viewProduct(
			ModelMap model,
			@PathVariable("cateID") Integer cateID,
            @PathVariable("pageNo") Integer pageNo,
            @RequestParam(name = "orderby", defaultValue = "menu_order") String orderby,
            @RequestParam(name="min_price", defaultValue = "0") int min_price,
            @RequestParam(name="max_price", defaultValue = "0") int max_price,
            @RequestParam(name="brandID", defaultValue = "0") Integer brandID) {
		
		Optional<Category> optCate1 = categoryService.findById(cateID);
		if (optCate1.isPresent()) {
			Category cate = optCate1.get();
			model.addAttribute("cate", cate);
		}
		List<Category> listCate = categoryService.findAll();
		model.addAttribute("listAllCategory", listCate);	
		List<Brand> listBrand = brandService.findAll();
		model.addAttribute("listAllBrand", listBrand);	
		int pageSize = 21;
		int totalProducts = proService.findAll().size(); // Số lượng sản phẩm tổng cộng trong cơ sở dữ liệu
		int totalPages = (int) Math.ceil((double) totalProducts / pageSize);
		if (cateID == 0) {

			Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
			Page<Product> productPage  = proService.findAll(pageable);
			List<Product> listPro = new ArrayList<>(productPage.getContent()); 
			if (brandID != 0) {
				listPro = listPro.stream()
			            .filter(product -> product.getBrand().getBrandId().equals(brandID))
			            .collect(Collectors.toList());
				totalProducts = listPro.size();
			}
			if (min_price == 0 && max_price == 0) {
				model.addAttribute("cateID", cateID);
				model.addAttribute("countPro", totalProducts);	
				
				if (orderby.equals("menu_order")) {
					model.addAttribute("listAllProduct", listPro);	
				}
				else if (orderby.equals("selling")) {
					Collections.sort(listPro, Comparator.comparingDouble(Product::getStock));
					model.addAttribute("listAllProduct", listPro);
				}
				else if (orderby.equals("date")) {
					Collections.reverse(listPro);
					model.addAttribute("listAllProduct", listPro);
				}
				else if (orderby.equals("price")) {
					Collections.sort(listPro, Comparator.comparingDouble(Product::getPrice));
					model.addAttribute("listAllProduct", listPro);
				}
				else if (orderby.equals("price-desc")) {
					Collections.sort(listPro, Comparator.comparingDouble(Product::getPrice).reversed());
					model.addAttribute("listAllProduct", listPro);
				}
				model.addAttribute("currentPage", pageNo);

				double minPriceSale = listPro.stream()
				        .mapToDouble(product -> product.getPrice() * (100 - product.getSale()) / 100)
				        .min()
				        .orElse(0);
				// Tính giá bán tối đa sau khi giảm giá
				double maxPriceSale = listPro.stream()
				        .mapToDouble(product -> product.getPrice() * (100 - product.getSale()) / 100)
				        .max()
				        .orElse(0);

				int minPriceSaleInt = (int) Math.round(minPriceSale);
				int maxPriceSaleInt = (int) Math.round(maxPriceSale);
				model.addAttribute("min_price", minPriceSaleInt);
				model.addAttribute("max_price", maxPriceSaleInt);
				model.addAttribute("min_form", minPriceSaleInt);
				model.addAttribute("max_form", maxPriceSaleInt);
			}
			else {
				List<Product> filteredList = new ArrayList<Product>();
				if (brandID != 0) {
					filteredList = listPro.stream()
							.filter(product -> Math.round(product.getPrice() * (100 - product.getSale()) / 100) >= min_price
									&& Math.round(product.getPrice() * (100 - product.getSale()) / 100) <= max_price
									&& product.getBrand().getBrandId().equals(brandID))
							.collect(Collectors.toList());
				} else {
					filteredList = listPro.stream()
							.filter(product -> Math.round(product.getPrice() * (100 - product.getSale()) / 100) >= min_price
									&& Math.round(product.getPrice() * (100 - product.getSale()) / 100) <= max_price)
							.collect(Collectors.toList());
				}
				
				if (orderby.equals("menu_order")) {
					model.addAttribute("listAllProduct", filteredList);
				}			
				else if (orderby.equals("selling")) {
					Collections.sort(filteredList, Comparator.comparingDouble(Product::getStock));
					model.addAttribute("listAllProduct", filteredList);
				}
				else if (orderby.equals("date")) {
					Collections.reverse(filteredList);
					model.addAttribute("listAllProduct", filteredList);
				}
				else if (orderby.equals("price")) {
					Collections.sort(filteredList, Comparator.comparingDouble(Product::getPrice));
					model.addAttribute("listAllProduct", filteredList);
				}
				else if (orderby.equals("price-desc")) {
					Collections.sort(filteredList, Comparator.comparingDouble(Product::getPrice).reversed());
					model.addAttribute("listAllProduct", filteredList);
				}
				model.addAttribute("cateID", cateID);
				model.addAttribute("countPro", filteredList.size());
				model.addAttribute("currentPage", pageNo);

				double minPriceSale = listPro.stream()
				        .mapToDouble(product -> product.getPrice() * (100 - product.getSale()) / 100)
				        .min()
				        .orElse(0);

				// Tính giá bán tối đa sau khi giảm giá
				double maxPriceSale = listPro.stream()
				        .mapToDouble(product -> product.getPrice() * (100 - product.getSale()) / 100)
				        .max()
				        .orElse(0);

				int minPriceSaleInt = (int) Math.round(minPriceSale);
				int maxPriceSaleInt = (int) Math.round(maxPriceSale);
				model.addAttribute("min_price", minPriceSaleInt);
				model.addAttribute("max_price", maxPriceSaleInt);
				model.addAttribute("min_form", min_price);
				model.addAttribute("max_form", max_price);
			}
			int startPage, endPage;
			totalPages = (int) Math.ceil((double) totalProducts / pageSize);
		    if (totalPages <= 5) {
		        startPage = 1;
		        if (totalPages > 0)
		        	endPage = totalPages;
		        else {
					endPage = 1;
					totalPages = 1;
				}
		    } else {
		        if (pageNo <= 3) {
		            startPage = 1;
		            endPage = 5;
		        } else if (pageNo + 1 >= totalPages) {
		            startPage = totalPages - 4;
		            endPage = totalPages;
		        } else {
		            startPage = pageNo - 2;
		            endPage = pageNo + 2;
		        }
		    }
			model.addAttribute("startPage", startPage);
		    model.addAttribute("endPage", endPage);
		    model.addAttribute("lastPage", totalPages);
			if (pageNo > totalPages) {
			    pageNo = totalPages; // Đặt pageNo bằng totalPages nếu vượt quá số trang thực tế
			}
		}
		else {		
			Optional<Category> optCate = categoryService.findById(cateID);
			if (optCate.isPresent()) {
				 pageSize = 27;
				 totalProducts = proService.findByCategory(optCate.get()).size(); // Số lượng sản phẩm tổng cộng trong cơ sở dữ liệu
				 totalPages = (int) Math.ceil((double) totalProducts / pageSize);
				
				int startPage, endPage;
				if (totalPages <= 5) {
					startPage = 1;
					endPage = totalPages;
					if (totalPages > 0)
						endPage = totalPages;
					else {
						endPage = 1;
						totalPages = 1;
					}
			    } else {
			        if (pageNo <= 3) {
			            startPage = 1;
			            endPage = 5;
			        } else if (pageNo + 1 >= totalPages) {
			            startPage = totalPages - 4;
			            endPage = totalPages;
			        } else {
			            startPage = pageNo - 2;
			            endPage = pageNo + 2;
			        }
			    }
				model.addAttribute("startPage", startPage);
			    model.addAttribute("endPage", endPage);
			    model.addAttribute("lastPage", totalPages);
				if (pageNo > totalPages) {
				    pageNo = totalPages; // Đặt pageNo bằng totalPages nếu vượt quá số trang thực tế
				}
				if (min_price == 0 && max_price == 0) {					
					Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
					Page<Product> productPage = proService.findByCategory(optCate.get(),pageable);	
					List<Product> listPro = new ArrayList<>(productPage.getContent());
					if (brandID != 0) {
						listPro = listPro.stream()
					            .filter(product -> product.getBrand().getBrandId().equals(brandID))
					            .collect(Collectors.toList());
					}
					model.addAttribute("cateID", cateID);
					model.addAttribute("countPro", listPro.size());
					if (orderby.equals("menu_order")) {
						model.addAttribute("listAllProduct", listPro);
	
					}
					else if (orderby.equals("selling")) {
						Collections.sort(listPro, Comparator.comparingDouble(Product::getStock));
						model.addAttribute("listAllProduct", listPro);

					}
					else if (orderby.equals("date")) {
						Collections.reverse(listPro);
						model.addAttribute("listAllProduct", listPro);
	
					}
					else if (orderby.equals("price")) {
						Collections.sort(listPro, Comparator.comparingDouble(Product::getPrice));
						model.addAttribute("listAllProduct", listPro);

					}
					else if (orderby.equals("price-desc")) {
						Collections.sort(listPro, Comparator.comparingDouble(Product::getPrice).reversed());
						model.addAttribute("listAllProduct", listPro);

					}
					model.addAttribute("currentPage", pageNo);
					double minPriceSale = listPro.stream()
					        .mapToDouble(product -> product.getPrice() * (100 - product.getSale()) / 100)
					        .min()
					        .orElse(0);

					// Tính giá bán tối đa sau khi giảm giá
					double maxPriceSale = listPro.stream()
					        .mapToDouble(product -> product.getPrice() * (100 - product.getSale()) / 100)
					        .max()
					        .orElse(0);

					int minPriceSaleInt = (int) Math.round(minPriceSale);
					int maxPriceSaleInt = (int) Math.round(maxPriceSale);
					model.addAttribute("min_price", minPriceSaleInt);
					model.addAttribute("max_price", maxPriceSaleInt);
					model.addAttribute("min_form", minPriceSaleInt);
					model.addAttribute("max_form", maxPriceSaleInt);
				}
				else {					
					Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
					Page<Product> listPro = proService.findByCategory(optCate.get(),pageable);
					List<Product> filteredList = new ArrayList<Product>();
					if (brandID != 0) {
						filteredList = listPro.getContent().stream()
						    .filter(product -> Math.round(product.getPrice() * (100-product.getSale()) / 100) >= min_price && Math.round(product.getPrice() * (100-product.getSale()) / 100) <= max_price && product.getBrand().getBrandId().equals(brandID))
						    .collect(Collectors.toList());
					}
					else {
						filteredList = listPro.getContent().stream()
							    .filter(product -> Math.round(product.getPrice() * (100-product.getSale()) / 100) >= min_price && Math.round(product.getPrice() * (100-product.getSale()) / 100) <= max_price)
							    .collect(Collectors.toList());
					}

					if (orderby.equals("menu_order")) {
						model.addAttribute("listAllProduct", filteredList);
					}
					else if (orderby.equals("selling")) {
						Collections.sort(filteredList, Comparator.comparingDouble(Product::getStock));
						model.addAttribute("listAllProduct", filteredList);
					}
					else if (orderby.equals("date")) {
						Collections.reverse(filteredList);
						model.addAttribute("listAllProduct", filteredList);
					}
					else if (orderby.equals("price")) {
						Collections.sort(filteredList, Comparator.comparingDouble(Product::getPrice));
						model.addAttribute("listAllProduct", filteredList);
					}
					else if (orderby.equals("price-desc")) {
						Collections.sort(filteredList, Comparator.comparingDouble(Product::getPrice).reversed());
						model.addAttribute("listAllProduct", filteredList);
					}
					model.addAttribute("cateID", cateID);
					model.addAttribute("countPro", filteredList.size());
					
					model.addAttribute("currentPage", pageNo);
					double minPriceSale = listPro.stream()
					        .mapToDouble(product -> product.getPrice() * (100 - product.getSale()) / 100)
					        .min()
					        .orElse(0);

					// Tính giá bán tối đa sau khi giảm giá
					double maxPriceSale = listPro.stream()
					        .mapToDouble(product -> product.getPrice() * (100 - product.getSale()) / 100)
					        .max()
					        .orElse(0);

					int minPriceSaleInt = (int) Math.round(minPriceSale);
					int maxPriceSaleInt = (int) Math.round(maxPriceSale);
					model.addAttribute("min_price", minPriceSaleInt);
					model.addAttribute("max_price", maxPriceSaleInt);
					model.addAttribute("min_form", min_price);
					model.addAttribute("max_form", max_price);
				}			    
			}	
		}
		model.addAttribute("orderby", orderby);
		model.addAttribute("brandID", brandID);
		model.addAttribute("selectedCategoryId", cateID);
		return "web/product";
	}
	@GetMapping("detail/{proId}")
	public ModelAndView detailProduct(ModelMap model, @PathVariable("proId") Integer proId) {
	
		Optional<Product> optProduct = proService.findById(proId);
		
		ProductModel proModel = new ProductModel();
	
		Product entity = optProduct.get();

		BeanUtils.copyProperties(entity, proModel);

		proModel.setIsEdit(true);
		
		Category enityCate = entity.getCategory();
		
		Brand enityBrand = entity.getBrand();

		List<Product> listBrand = proService.findByBrand(enityBrand.getBrandId(), proModel.getPrice());
		List<Product> listCate = proService.findByCategory(enityCate.getCateId(), proModel.getPrice());
		
		String input = proModel.getDescription();
		String description = input;
		int index = input.indexOf("Thành phần sản phẩm");

	    if (index != -1) {
	        String thanhPhanSanPham = input.substring(index + "Thành phần sản phẩm".length()).trim();
	        description = thanhPhanSanPham;
	    }
		LocalDate localDate = LocalDate.now();

        String formattedDate = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		
        int userId = -1;
        ShoppingSession entitySS = new ShoppingSession();
        ShoppingSessionKey ssKey = new ShoppingSessionKey();
        Object authen = SecurityContextHolder.getContext().getAuthentication() .getPrincipal();
        if (authen instanceof AuthUser) {
	        String email = ((AuthUser)authen) .getEmail();
	        Optional<User> optUser = userService.findByEmail(email);
	        if (optUser.isPresent()) {
	        	UserModel userModel = new UserModel();
	        	User user = optUser.get();
	        	BeanUtils.copyProperties(user, userModel);
	        	userModel.setIsEdit(true);
	        	userId = userModel.getUserId();
	        	entitySS.setUser(user);
	        	entitySS.setId(ssKey);
	            entitySS.setProduct(entity);
	            entitySS.setDate(formattedDate);
	            List<Cart> entityCart = cartService.findByUserId(userId);
	            int quantityCart = 0;
	            for (Cart cart : entityCart) {
					if(cart.getProduct().getProId()-proId ==0) {
						quantityCart = cart.getQuantity();	
					}
				}
	            System.out.println(quantityCart);
	            model.addAttribute("quantityCart", quantityCart);
	        }
        }
        int flagSave = 1;
        if(userId == -1) {
        	flagSave = 0;
        }
        
        //System.out.println(proId);
        List<ShoppingSession> listSS = ssService.findByUser(userId);
        List<ShoppingSession> listSSAll = ssService.findAll();
        for (ShoppingSession shoppingSession : listSSAll) {
        	Product pro = shoppingSession.getProduct();
        	System.out.println(pro.getProId());
			if(pro.getProId() - proId == 0) {
				flagSave = 0;
				System.out.println("yes");
				break;
			}
				
		}
        if(flagSave > 0) {
        	ssService.save(entitySS);
        }
		List<Product> listProSeen = new ArrayList<Product>();
		for (ShoppingSession shoppingSession : listSS) {
			Product proSeen = shoppingSession.getProduct();
			listProSeen.add(proSeen);
		}
           
		proModel.setOldPrice(proModel.getPrice());
		proModel.setPrice(Math.round(proModel.getPrice() * (100 - proModel.getSale()) / 100));
		String brandName = enityBrand.getName();
		String cateName = enityCate.getName();
		
		
		model.addAttribute("description", description);
		model.addAttribute("isHasUser", userId);
		model.addAttribute("cateName",cateName);
		model.addAttribute("brandName", brandName);
		model.addAttribute("listProSeen", listProSeen);
		model.addAttribute("listCate", listCate);
		model.addAttribute("detailPro", proModel);
		model.addAttribute("listBrand", listBrand);
		return new ModelAndView("web/product-detail", model);
		
	}
	@GetMapping(value = "add-to-cart/{proId}&&{qty}")
	public String addToCart(ModelMap model, @PathVariable("proId") Integer proId, @PathVariable("qty") Integer qty) {
		Optional<Product> optProduct = proService.findById(proId);
		
		ProductModel proModel = new ProductModel();
	
		Product entity = optProduct.get();

		BeanUtils.copyProperties(entity, proModel);

		proModel.setIsEdit(true);
		User userLogged = userService.getUserLogged();
		
		CartKey cartkey = new CartKey();
		Cart entityCart = new Cart();

		List<Cart> list = cartService.findByUserId(userLogged.getUserId());
		int quatity = qty;
		for (Cart cart : list) {
			System.out.println(cart.getProduct().getProId());
			if(cart.getProduct().getProId() - proId == 0) {
				if (cart.getQuantity() >= cart.getProduct().getStock()) {
					return "redirect:/cart";
				}
				quatity = cart.getQuantity()+qty;
				cart.setQuantity(quatity);
				cartService.save(cart);
				return "redirect:/cart";
			}
		}
		entityCart.setProduct(entity);
		entityCart.setUser(userLogged);
		entityCart.setId(cartkey);
		entityCart.setQuantity(qty);
		
		cartService.addtocart(entityCart.getProduct().getProId(), entityCart.getUser().getUserId(), entityCart.getQuantity());
		return "redirect:/cart";
	}
	
	@GetMapping(value = "addToCart/{proId}&&{qty}")
    public ResponseEntity<String> addToCart(@PathVariable("proId") Integer proId, @PathVariable("qty") Integer qty) {
        try {
        	String successMessage = "Thêm vào giỏ hàng thành công";
        	
        	Optional<Product> optProduct = proService.findById(proId);
    		
    		ProductModel proModel = new ProductModel();
    	
    		Product entity = optProduct.get();

    		BeanUtils.copyProperties(entity, proModel);

    		proModel.setIsEdit(true);
    		User userLogged = userService.getUserLogged();
    		
    		CartKey cartkey = new CartKey();
    		Cart entityCart = new Cart();

    		List<Cart> list = cartService.findByUserId(userLogged.getUserId());
    		int quatity = qty;
    		for (Cart cart : list) {
    			System.out.println(cart.getProduct().getProId());
    			if(cart.getProduct().getProId() - proId == 0) {
    				if (cart.getQuantity() == cart.getProduct().getStock()) {
    					String errorMessage = "Số lượng sản phẩm trong giỏ hàng lớn hơn số lượng tồn kho.";
    					return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    				}
    				quatity = cart.getQuantity()+qty;
    				cart.setQuantity(quatity);
    				cartService.save(cart);
    				return new ResponseEntity<>(successMessage, HttpStatus.OK);
    			}
    		}
    		entityCart.setProduct(entity);
    		entityCart.setUser(userLogged);
    		entityCart.setId(cartkey);
    		entityCart.setQuantity(qty);
    		
    		cartService.addtocart(entityCart.getProduct().getProId(), entityCart.getUser().getUserId(), entityCart.getQuantity());
            
            return new ResponseEntity<>(successMessage, HttpStatus.OK);
            
        } catch (Exception e) {
            String errorMessage = "Error adding product to cart: " + e.getMessage();
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
