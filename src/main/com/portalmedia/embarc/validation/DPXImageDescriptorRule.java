package com.portalmedia.embarc.validation;

import com.portalmedia.embarc.gui.model.FileInformationViewModel;
import com.portalmedia.embarc.parser.dpx.DPXColumn;

/**
 * For each of the specified image elements, verify the image descriptor is
 * between 0-156
 *
 * @author PortalMedia
 * @since 2019-05-01
 **/
public class DPXImageDescriptorRule implements IFileValidationRule {
	DPXColumn targetColumn;
	boolean isValid;
	int imageElement = 1;
	private final String valid8 = "Must be valid unsigned 8bit integer";

	private final String partUndefWarn = "= warning that a non-defined value is in use; ";
	private final String fullUndefWarn = partUndefWarn + " cannot = 255";
	private final String smc = "SMPTE-C \n\n";

	public DPXImageDescriptorRule(DPXColumn column, int imageElement) {
		this.targetColumn = column;
		this.imageElement = imageElement;
	}

	@Override
	public String getRule() {
		return smc + valid8 + "; can be values of 0-156; 157-254 " + fullUndefWarn;
	}

	@Override
	public ValidationRuleSetEnum getRuleSet() {
		return ValidationRuleSetEnum.SMPTE_C;
	}

	@Override
	public DPXColumn getTargetColumn() {
		return targetColumn;
	}

	@Override
	public IValidationRule getValidationRule() {
		return new IsValidByteRangeRule((byte) 0, (byte) 156);
	}

	@Override
	public boolean isValid(FileInformationViewModel file) {

		final String imageElements = file.getProp(DPXColumn.NUMBER_OF_IMAGE_ELEMENTS);
		if (imageElements.isEmpty()) {
			return true;
		}
		try {
			if (Integer.parseInt(imageElements) < imageElement) {
				return true;
			}
		} catch (final NumberFormatException ex) {
			return true;
		}

		final String value = file.getProp(targetColumn);

		final IValidationRule rule = getValidationRule();
		return rule.isValid(value);
	}
}
