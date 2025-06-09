package com.code.share.codesharing.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ToolsAndEquipmentLedger {
    private Integer teLedgerId;
    private String category;
    private int categoryId;
    private String materialCode;
    private Integer materialNum;
    private String materialCode2;
    private Integer materialNum2;
    private String loa;
    private int loaId;
    private String teName;
    private int teNameId;
    private String brandAndModel;
    private int brandAndModelId;
    private String model;
    private int modelId;
    private String specification;
    private LocalDate datePurchased;
    private int lifeSpan;
    private String isLifeSpan;
    private String mosDayUsed;
    private BigDecimal price;
    private BigDecimal remainingValue;
    private String serialNumber;
    private String projectName;
    private String projectId;
    private String costCenter;
    private String wbs;
    private String projectManager;
    private String projectManagerId;
    private String keeper;
    private String keeperId;
    private String keeperDept;
    private String companyId;
    private String region;
    private String regionId;
    private String areaLocation;
    private String areaId;
    private String location;
    private String teamCategory;
    private String storageLocation;
    private String status;
    private int statusId;
    private String pictureStatus;
    private int pictureStatusId;
    private String remarks;
    private String insertBy;
    private LocalDateTime insertedDate;
    private LocalDateTime updatedDate;
    private String userUpdated;
    private int userUpdatedId;
    private int uploadType;
    private String img_status;

    private String state;
    private String stateName;

    private Double salvagePrice; //Price based on duration between purchased date and current date

    private Double depreciatedPriceIr; //Price based on duration between purchased date and IR Date

    private Integer realUseLife;
    private int isQa;
    private int isIr;
    private LocalDateTime irDate;

    public void calculateDepreciatedValue() {
        try {
            LocalDate startDate = this.getDatePurchased();
            LocalDate endDate = LocalDate.now();

            long duration = (ChronoUnit.DAYS.between(startDate, endDate))/30;
            double value1 = this.getPrice().floatValue()*0.03;
            double value2 = this.getPrice().floatValue() - value1;
            int totalLifeSpan = this.getLifeSpan()*12;

            if((int)duration > totalLifeSpan) {
                double salvageValue2 = this.roundOff(value1 + (value1 * 0.12));
                this.setSalvagePrice(salvageValue2);
                this.setDepreciatedPriceIr(salvageValue2);
            } else {
                double salvageValue = this.getPrice().floatValue() - (duration * (value2 / totalLifeSpan));
                double totalDepreciation = salvageValue + (salvageValue * 0.12);
                this.setSalvagePrice(this.roundOff(totalDepreciation));

                if (this.getIrDate() != null) {
                    //Duration between purchased date and IR date
                    long durationIr = (ChronoUnit.MONTHS.between(startDate, this.getIrDate()));
                    this.setRealUseLife((int) durationIr);

                    //Calculate depreciated price
                    double depreciatedPrice = this.getPrice().floatValue() - (durationIr * (value2 / totalLifeSpan));
                    double totalDepreciationIr = depreciatedPrice + (depreciatedPrice * 0.12);
                    this.setDepreciatedPriceIr(this.roundOff(totalDepreciationIr));
                }
            }

            if (this.getIrDate() != null) {
                long realUseDur = ChronoUnit.MONTHS.between(startDate, this.getIrDate());
                this.setRealUseLife((int)realUseDur);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("calculateDepreciatedValue.e: " + ExceptionUtils.getStackTrace(e));
        }
    }
    public double roundOff(double dbl) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.UP);
        return Double.parseDouble(df.format(dbl));
    }
}
