package net.itsjustsomedude.tokens.db;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import net.itsjustsomedude.tokens.AppDatabase;

import java.util.List;

public class CoopModel extends AndroidViewModel {
	private final List<Coop> allCoops;
	private final CoopDao dao;

	public CoopModel(@NonNull Application application) {
		super();
		dao = AppDatabase.getInstance(application).coopDao();
		allCoops = dao.getAllCoops();
	}

	public void insert(Coop coop) {
		dao.insert(coop);
	}

	public void update(Coop coop) {
		dao.update(coop);
	}

	public void delete(Coop coop) {
		dao.delete(coop);
	}

	public List<Coop> getAllCoops() {
		return allCoops;
	}
}
