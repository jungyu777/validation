package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";

    }

    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes,Model model) {

        //어떤 오류가 있는지 담아줄 객체가 필요하다
        //검증 오류 결과를 보관
        Map<String,String> errors =new HashMap<>();
        //검증로직 /만약 아이템네임에 글자가 없으면실행되는 로직!!
        if(!StringUtils.hasText(item.getItemName())){
            errors.put("itemName","상품 이름은 필수 입니다.");
        }
        //요구사항중에 1000원에서 1000000만원 사이의 값만 들어와 야 한다 만약 이경우에 벗어나는 경우에는 ㅇㅓ떻게 할것인지 밑어 적어보자
        if (item.getPrice( )==null || item.getPrice() < 1000 || item.getPrice() > 1000000){
            errors.put("price","가격은 1,000 ~ 1,000,000 까지 허용합니다");
        }
        //수량은 null 또는 9999개 를 넘으면 안된다
        if(item.getQuantity() == null || item.getQuantity() > 9999){
            errors.put("quantity","수량은 최대 9,999 까지  허용합니다");
        }
        //뷰에서 랜더링 할떄  errors 무엇인가  값이 있으면 오류가 뜨게 보여주면된다

        //특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000){
                errors.put("globalError"," 가격 곱하기 수량의 합은 10,000원 이상이어야 합니다. 현재 값 = " + resultPrice);
            }
        }
        //검증에 실패하면 다시 입력 폼으로  에러가 없음이 아니면  부정의부정 , 검증에 실패하면 다시 입력폼으로 보낸다
        log.info("errors ={} ", errors);
        if (!errors.isEmpty()){
            model.addAttribute("errors",errors);
            return "validation/v2/addForm";
        }
        //errors 안걸리면  성공로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

}

