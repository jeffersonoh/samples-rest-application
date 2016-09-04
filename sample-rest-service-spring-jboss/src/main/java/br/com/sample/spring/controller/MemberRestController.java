package br.com.sample.spring.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import br.com.sample.spring.data.MemberDao;
import br.com.sample.spring.model.Member;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="Client sample-spring-jboss API")
@Controller
@RequestMapping("/rest/members")
public class MemberRestController {
    @Autowired
    private MemberDao memberDao;

    @ApiOperation(value="Find members for system ", notes="Search synchronous")
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<Member> listAllMembers() {
        return memberDao.findAllOrderedByName();
    }

    @ApiOperation(value="Find members for system id", notes="Search synchronous")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Member lookupMemberById(@PathVariable("id") Long id) {
        return memberDao.findById(id);
    }
    
    @ApiOperation(value="Sorted members for member", notes="Search synchronous")
    @RequestMapping(method = RequestMethod.GET)
    public String displaySortedMembers(Model model) {
        model.addAttribute("newMember", new Member());
        model.addAttribute("members", memberDao.findAllOrderedByName());
        return "index";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String registerNewMember(@Valid @ModelAttribute("newMember") Member newMember, BindingResult result, Model model) {
        if (!result.hasErrors()) {
            try {
                memberDao.register(newMember);
                return "redirect:/";
            } catch (UnexpectedRollbackException e) {
                model.addAttribute("members", memberDao.findAllOrderedByName());
                model.addAttribute("error", e.getCause().getCause());
                return "index";
            }
        } else {
            model.addAttribute("members", memberDao.findAllOrderedByName());
            return "index";
        }
    }
    
    @ApiOperation(value="Remove member for system ", notes="Remove synchronous")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String  removeMember(@PathVariable("id") Long id) {
    	memberDao.remove(id);
        return "OK";
    }
}
