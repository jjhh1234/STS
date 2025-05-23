package org.ex.back.domain.menu.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class OptionItemDTO {
    private Integer option_item_pk;

    private String name;

    private Integer extraPrice;

    //옵션 아이템 엔티티 -> 옵션 아이템 DTO 변환을 위한 생성자 (stream API)
    public OptionItemDTO(Integer item_pk, String name, Integer extraPrice) {
        this.option_item_pk = item_pk;
        this.name = name;
        this.extraPrice = extraPrice;
    }
    //로그 출력을 위한 toString메소드
//    @Override
//    public String toString() {
//        return "OptionItemDTO{" +
//                "option_item_pk=" + id +
//                ", name='" + name + '\'' +
//                ", extraPrice=" + extraPrice +
//                '}';
//                }






}
