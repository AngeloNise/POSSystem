package com.microservice.systemservice.services;

import com.microservice.systemservice.models.ContactAssets;
import com.microservice.systemservice.repository.ContactAssetsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactAssetsService {

    private final ContactAssetsRepository contactAssetsRepository;

    Function<ContactAssets, List<Object>> compositeKey = contact ->
            Arrays.asList(contact.getProject(), contact.getProjectLeadName(), contact.getRegion(),
                    contact.getProjectLeadWeChatId(),
                    contact.getPlatformManagerAndAdminManager(), contact.getPmWeChatId());

    public List<ContactAssets> getAllContactAssetList() {
        List<ContactAssets> contactAssets = contactAssetsRepository.loadContactAssetList();
        Long groupId = 0L;
        Map<Object, List<ContactAssets>> map = contactAssets.stream().collect(
                Collectors.groupingBy(compositeKey, Collectors.toList()));

        map.entrySet().stream().forEach(e -> {
                e.getValue().stream()
                        .collect(
                                Collectors.groupingBy(ContactAssets::getProjectLeadName));
        });
        for(var entry : map.entrySet()){
            groupId = entry.getValue().stream().peek(ContactAssets::getId).findFirst().get().getId();
            Long finalGroupId = groupId;
            entry.getValue().stream().skip(1).forEach(o-> {
                //o.setProject("");
                o.setGroupId(finalGroupId);
                o.setProjectLeadName("");
                o.setProjectLeadWeChatId("");
                o.setRegion("");
                o.setPlatformManagerAndAdminManager("");
                o.setPmWeChatId("");
            });
        }

        return map.values().stream().flatMap(List::stream).sorted(Comparator.comparing(ContactAssets::getProject))
                .collect(Collectors.toList());
    }

    public void updateContactAsset(long id, ContactAssets contactAssets) {
        int updated = contactAssetsRepository.updateContactAssetList(id,contactAssets);
    }

    public void saveContactList(ContactAssets contactAsset) {
        int inserted = contactAssetsRepository.saveContactAssetList(contactAsset);
    }

    public ContactAssets updateContactListProject(Long id, String newProjectName) {

        return contactAssetsRepository.updateContactListProject(id,newProjectName);
    }

    public void deleteContactAssetInfo(Long id) {
        int deleted = contactAssetsRepository.deleteSelectedContactAssetList(id);
    }

    public void modifyNewProjectName(Map<String, Object> requestMap) {
        int updated = contactAssetsRepository.updateContactListProject(requestMap);
    }
}
