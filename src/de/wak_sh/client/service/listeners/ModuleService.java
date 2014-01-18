package de.wak_sh.client.service.listeners;

import java.io.IOException;
import java.util.List;

import de.wak_sh.client.model.Module;

public interface ModuleService {
	public List<Module> getModules() throws IOException;
}
