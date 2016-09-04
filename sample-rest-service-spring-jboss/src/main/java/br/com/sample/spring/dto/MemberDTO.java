package br.com.sample.spring.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


//@ApiModel – Usada para nomear a classe do modelo.
//@ApiModelProperty – Usada para descrever uma propriedade dentro da classe modelo.

@ApiModel(value = "MemberDTO")
public class MemberDTO implements Serializable {

	private static final long serialVersionUID = 395662958596024782L;
	
	@ApiModelProperty(value = "Id do cliente.", required=true)
	private Long id ;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
