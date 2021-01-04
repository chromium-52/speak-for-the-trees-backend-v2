package com.codeforcommunity.dto.imports;

import com.codeforcommunity.dto.ApiDto;
import com.codeforcommunity.exceptions.HandledException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ImportBlocksRequest extends ApiDto {
  private List<BlockImport> blocks;

  public ImportBlocksRequest(List<BlockImport> blocks) {
    this.blocks = blocks;
  }

  private ImportBlocksRequest() {}

  public List<BlockImport> getBlocks() {
    return blocks;
  }

  public void setBlocks(List<BlockImport> blocks) {
    this.blocks = blocks;
  }

  @Override
  public List<String> validateFields(String fieldPrefix) throws HandledException {
    String newFieldPrefix = fieldPrefix + "importNeighborhoodRequest.";

    if (blocks == null) {
      return Collections.singletonList(newFieldPrefix + "blocks");
    }

    return blocks.stream()
        .flatMap(ni -> ni.validateFields(newFieldPrefix).stream())
        .collect(Collectors.toList());
  }
}
