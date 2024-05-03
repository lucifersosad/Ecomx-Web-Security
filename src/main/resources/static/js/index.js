$(document).ready(function() {
	var originalTotalValue = $("#subTotalValue").html();

	$("#cod").on("click", function() {
		$("#codOption").css("display", "block");
		var codHtml = $("#codValue").html();
		var codValue = parseFloat(codHtml.replace(/\D/g, ''));

		var totalHtml = $("#totalValue").html();
		var totalValue = parseFloat(totalHtml.replace(/\D/g, ''));

		$("#totalValue").html((codValue + totalValue).toLocaleString('vi-VN') + ' đ');
		console.log('giá vận chuyển : ', $("#subTotalValue").html());
		console.log('giá : ', codValue);
		console.log('giá cuối: ', codValue + totalValue);
		$("#codOption").css("font-weight", "bold");
		$("#total").css("font-weight", "bold");
	});

	$(document).on("click", function(e) {
		var clickedElement = $(e.target);
		if (clickedElement.is("#vnpay") || clickedElement.is("#paypal")) {
			$("#totalValue").html(originalTotalValue);
			$("#codOption").css("display", "none");
			$("#total").css("font-weight", "normal");
			//gọi đến hành động bên dưới
			$('#applydiscount').trigger('click');
		}
	});
	//----------------------- xử lí hiển thị liên quan giảm giá checkout
	$('#applydiscount').on('click', function() {
		var totalValue = $("#subTotalValue").html();
		var cleanPrice = totalValue.replace(/\D/g, '');
		var subtotal = parseFloat(cleanPrice);

		var totalHtml = $("#totalValue").html();
		var totalValues = parseFloat(totalHtml.replace(/\D/g, ''));
		var totalValueFloat = parseFloat(totalValues);

		var promoCode = $('#promoInput').val();
		if (promoCode !== "") {
			$.ajax({
				url: '/CheckOut/DiscountPost',
				method: 'POST',
				dataType: 'json',
				data: { promo: promoCode },
				success: function(data) {
					var receivedInteger = parseFloat(data);
					if (receivedInteger === 2) {
						$('#collapseExample').collapse('hide');
						$('#error').text('Áp dụng mã không thành công');
					}
					else if (receivedInteger === 0) {
						$('#promoInput').val('');
						$('#collapseExample').collapse('hide');
						$('#error').text('Mã đã được dùng');
					}
					else {
						var afterDiscount = receivedInteger * subtotal;
						var total = afterDiscount.toLocaleString('vi-VN');
						var totalafter = ((totalValueFloat) - afterDiscount).toLocaleString('vi-VN');

						$('#discountvalue').text('-' + total + ' đ');

						$("#totalValue").html(totalafter + ' đ');

						$('#collapseExample').collapse('hide');
						$('#mess').text('Áp dụng mã thành công');
					}
					setTimeout(hideMessages, 1000);
				},
				error: function(error) {
					console.error('Lỗi khi gửi yêu cầu:', error);
					$('#collapseExample').collapse('hide');
					$('#error').text('Lỗi');
					setTimeout(hideMessages, 3000);
				}
			});
		}
	});

	$('#layma').click(function() {
		$.ajax({
			url: '/CheckOut/RandomDiscount',
			method: 'GET',
			success: function(data) {
				var collapseExample = $('#collapseExample');
				if (!collapseExample.hasClass('show')) {
					collapseExample.collapse('toggle');
				}
				$('#promoInput').val(data);
				console.log('ma giam gia', data);
			},
			error: function(error) {
				console.error('Lỗi khi lấy dữ liệu:', error);
			}
		});
	});
	$('#layma').one('click', function() {
		$(this).prop('disabled', true);
	});

	function hideMessages() {
		$("#mess").hide();
		$("#error").hide();
	}
	//----------------------------------

});


function updateQuantity(proid, qtt) {
	fetch('cart/updateQTT', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify({
			proid: proid,
			quantity: qtt
		}),
	})
		.then(response => response.text())
		.then(data => {
			console.log(data);
			location.reload();
		})
		.catch((error) => {
			console.error('Error:', error);
		});
}

$(document).on('click', function(event) {
	var target = event.target;
	if (target.classList.contains('btn-quantity')) {
		var row = target.closest('tr');

		var input = row.querySelector('.form-control');
		var inputValue = parseInt(input.value, 10);

		var proid = input.getAttribute('data-proid');
	    var stock = parseInt(input.getAttribute('data-stock'), 10);
	    console.log(stock);
		var currentQuantity = parseInt(row.querySelector('.form-control').value, 10);
		console.log(currentQuantity);
		if (target.classList.contains('js-btn-minus')) {
			if (currentQuantity == 1) {
				window.location.href = "cart/deleteItem/" + proid;
			}

			if (currentQuantity > 1) {
				updateQuantity(proid, inputValue - 1);
			}
		} else if (target.classList.contains('js-btn-plus')) {
			if (currentQuantity >= stock) {
				inputValue = stock;
			}
			else {
				updateQuantity(proid, inputValue + 1);
			}
		}
	}
});

function updateAddress() {
	var email = document.querySelector('#email').value;
	var fullName = document.querySelector('#fullName').value;
	var phone = document.querySelector('#phone').value;
	var city = document.querySelector('#city').value;
	var district = document.querySelector('#district').value;
	var town = document.querySelector('#town').value;
	var homeaddress = document.querySelector('#address').value;

	fetch('/web/users/updateAddress', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded',
		},
		body: 'email=' + email +
			'&fullName=' + fullName +
			'&phone=' + phone +
			'&city=' + city +
			'&district=' + district +
			'&town=' + town +
			'&homeaddress=' + homeaddress,
	})
		.then(response => response.text())
		.then(data => {
			localStorage.setItem('updatedAddress', JSON.stringify({
				email: email,
				fullName: fullName,
				phone: phone,
				city: city,
				district: district,
				town: town,
				homeaddress: homeaddress,
				data: data,
			}));
			location.reload();
		})
		.catch(error => console.error('Error:', error));
}
function updateUser() {
	$(".btn-update").css("display", "block");
	$("#fullName").prop("disabled", false);
	$("#phone").prop("disabled", false);
	$("#city").prop("disabled", false);
	$("#district").prop("disabled", false);
	$("#town").prop("disabled", false);
	$("#address").prop("disabled", false);
	$("#fullName").focus();
}

$(document).ready(function() {

	var updatedAddress = localStorage.getItem('updatedAddress');
	if (updatedAddress) {

		var addressInfo = JSON.parse(updatedAddress);
		$('#email').val(addressInfo.email);
		$('#fullName').val(addressInfo.fullName);
		$('#phone').val(addressInfo.phone);
		$('#city').val(addressInfo.city);
		$('#district').val(addressInfo.district);
		$('#town').val(addressInfo.town);
		$('#address').val(addressInfo.homeaddress);
		$('#thong-bao').html(addressInfo.data);
		console.log("OK");

		localStorage.removeItem('updatedAddress');
	}
});

$(document).ready(function() {
	var codInput = $('#cod');
	if (codInput.length) {
		codInput.click();
	}
});

function updateCartQuantity() {
	fetch('/cartQty')
		.then(response => response.json())
		.then(data => {
			if (data < 100) {
				$('#cartQty').html(data);
			} else {
				$('#cartQty').html("99+")
			}

		})
		.catch(error => {
			console.error('Error fetching cart quantity:', error);
		});
}

$(document).ready(function() {
	updateCartQuantity();
});

$(document).ready(function() {
	$('.buyNow').click(function(event) {
		event.preventDefault();
		var clickedElement = $(event.target);
		var proId = clickedElement.data('proid');
		var redirectUrl = '/web/product/add-to-cart/' + proId + '&&' + 1;
		window.location.href = redirectUrl;
	});
});

$(document).ready(function() {
	$('.addToCart').click(async function(event) {
		event.preventDefault();
		var clickedElement = $(event.target);
		var proId = clickedElement.data('proid');
		var qty = 1;

		try {
			const response = await fetch(`/web/product/addToCart/${proId}&&${qty}`, {
				method: 'GET',
				headers: {
					'Content-Type': 'application/json'
				},
			});

			if (!response.ok) {
				const errorMessage = await response.text();
				if (errorMessage.includes('Số lượng sản phẩm trong giỏ hàng lớn hơn số lượng tồn kho.'))
                	throw new Error('Số lượng sản phẩm trong giỏ hàng lớn hơn số lượng tồn kho.')
                else {
					throw new Error('Đăng nhập để tiếp tục');
				} 
			}

			const data = await response.text();
			console.log(data);
			updateCartQuantity();
			showSuccess();
		} catch (error) {
			showError(error);
		}
	});
});


function showSuccess(title) {
	Swal.fire({
		position: "top-end",
		icon: 'success',
		title: title || 'Thêm vào giỏ hàng thành công',
		timer: 1500,
		showConfirmButton: false,
		toast: true,
		timerProgressBar: true,
	})
};

function showError(text) {
	Swal.fire({
		icon: "error",
		title: "Lỗi",
		text: text || "Lỗi",
		showConfirmButton: false,
		showCancelButton: true,
		timer: 1500,
	});
}

$(document).ready(function() {
	$(".btn-back").on("click", function() {
		window.history.back();
	});
});

$(document).ready(function() {  
	console.log($("#range-price-min").val());
    $(".js-range-slider").ionRangeSlider({
		hide_min_max: true,
		hide_from_to: true,
		onChange: updateInputs,
	});
    
	function updateInputs(data) {
		var $inputFrom = $("#range-price-min");
		var $inputTo = $("#range-price-max");
		from = data.from;
		to = data.to;
		$inputFrom.prop("value", from);
		$inputTo.prop("value", to);
		$("#ui-price-min").html(from + ".000đ")
		$("#ui-price-max").html(to + ".000đ")
	}
});

$(document).ready(function() {
	$('.quantityInputCart').each(function() {
		var $input = $(this);
		$input.data('initial-value', $input.val());
	});
	$('.quantityInputCart').on('change', function() {
		var id = parseInt($(this).data('proid'))
		var newValue = parseInt($(this).val());
		var stock = parseInt($(this).data('stock'));
		console.log(newValue + " " + stock);

		if (isNaN(newValue) || newValue <= 0 || newValue > stock) {
			$(this).val($(this).data('initial-value'));
		} else {
			$(this).val(newValue);
			console.log(id);
			console.log("Giá trị mới hợp lệ:", newValue);
			updateQuantity(id, newValue);
		}
	});
});

