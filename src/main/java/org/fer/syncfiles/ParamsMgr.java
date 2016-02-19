package org.fer.syncfiles;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.fer.syncfiles.model.ParamList;

public class ParamsMgr {

	private static final String SYNC_FILES_PARAMS_XML = "./syncFilesParams.xml";

	public void saveParams(ParamList paramList) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(ParamList.class);
		Marshaller m = context.createMarshaller();
		m.marshal(paramList, new File(SYNC_FILES_PARAMS_XML));
	}
	
	public ParamList loadParams() throws JAXBException {
		ParamList res = null;
		JAXBContext context = JAXBContext.newInstance(ParamList.class);
		Unmarshaller um = context.createUnmarshaller();
		res = (ParamList) um.unmarshal(new File(SYNC_FILES_PARAMS_XML));
		return res;
	}
}
