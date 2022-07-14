package dev.practice.order.interfaces.partner;

import dev.practice.order.application.partner.PartnerFacade;
import dev.practice.order.config.auth.LoginUser;
import dev.practice.order.config.auth.dto.SessionUser;
import dev.practice.order.domain.partner.Partner;
import dev.practice.order.domain.partner.PartnerInfo;
import dev.practice.order.domain.partner.PartnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.rule.Mode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("partner")
@RequiredArgsConstructor
public class PartnerController {

    private final PartnerFacade partnerFacade;
    private final PartnerDtoMapper partnerDtoMapper;
    private final PartnerService partnerService;

    @GetMapping
    public String partnerMain(Model model) {
        return "partner/partnerMain";
    }

    @GetMapping("/list")
    public String partners(Model model) {
        List<Partner> partners = partnerService.getPartners();

        model.addAttribute("partners", partners);
        return "partner/partnerList";
    }


    @GetMapping("/enroll")
    public String enrollForm(Model model) {
        model.addAttribute("partner", new Partner());
        return "partner/enrollPartner";
    }

    @PostMapping("/enroll")
    public String enroll(@Valid @ModelAttribute("partner") PartnerDto.RegisterRequest request, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "partner/enrollPartner";
        }

        //저장
        var command = request.toCommand();
        PartnerInfo partnerInfo = partnerFacade.registerPartner(command);

        //상세 화면으로 이동
        redirectAttributes.addAttribute("partnerToken", partnerInfo.getPartnerToken());
        return "redirect:/partner/view/{partnerToken}";
    }

    @GetMapping("/view/{partnerToken}")
    public String partnerView(@PathVariable String partnerToken, Model model) {

        PartnerInfo partnerInfo = partnerFacade.getPartnerInfo(partnerToken);
        model.addAttribute("partner", partnerInfo);

        return "partner/viewPartner";
    }

    @GetMapping("/edit/{partnerToken}")
    public String partnerEditForm(@PathVariable String partnerToken, Model model) {

        PartnerInfo partnerInfo = partnerFacade.getPartnerInfo(partnerToken);

        model.addAttribute("partner", partnerInfo);
        return "partner/editPartner";
    }

    // 파트너 수정건 반영
    @PostMapping("/edit/{partnerToken}")
    public String partnerEdit(@PathVariable String partnerToken, @Valid @ModelAttribute("partner") PartnerDto.RegisterRequest request, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        //검증에 실패하면 다시 입력 폼으로
        if (bindingResult.hasErrors()) {
            log.info("errors={} ", bindingResult);
            return "partner/enrollPartner";
        }

        //저장
        var command = request.toCommand();
        partnerService.updatePartner(partnerToken, command); //방향만 맞다면 service를 바로 호출할 수 있다.

        //상세 화면으로 이동
        redirectAttributes.addAttribute("partnerToken", partnerToken);
        return "redirect:/partner/view/{partnerToken}";
    }
}
