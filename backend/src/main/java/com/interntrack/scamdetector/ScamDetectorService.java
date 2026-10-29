package com.interntrack.scamdetector;

import com.interntrack.ai.AiService;
import com.interntrack.ai.ScamAnalysisResult;
import com.interntrack.ai.ScamScanInput;
import com.interntrack.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScamDetectorService {

    private final ScamDetectorDao scamDetectorDao;
    private final AiService aiService;

    public ScamDetectorService(ScamDetectorDao scamDetectorDao, AiService aiService) {
        this.scamDetectorDao = scamDetectorDao;
        this.aiService = aiService;
    }

    @Transactional
    public ScamScan scanOffer(Long userId, ScamScanInput input) {
        ScamAnalysisResult result = aiService.analyzeScamRisk(input);
        Long scanId = scamDetectorDao.saveScan(userId, input, result);

        ScamScan scan = scamDetectorDao.findByIdAndUserId(scanId, userId).orElseThrow();
        scan.setRecommendedSteps(result.getRecommendedSteps());
        return scan;
    }

    public List<ScamScan> getHistory(Long userId) {
        return scamDetectorDao.findByUserId(userId);
    }

    public ScamScan getScanById(Long id, Long userId) {
        ScamScan scan = scamDetectorDao.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Scam scan not found or unauthorized"));

        // Attach standard recommended steps for UI
        scan.setRecommendedSteps(List.of(
                "Search the official corporate careers portal directly via a web browser.",
                "Check company registration on the Ministry of Corporate Affairs (MCA) or official national registrar.",
                "Connect with verified current employees of the organization on LinkedIn.",
                "Never send money, purchase gift cards, or provide bank OTPs under any circumstances.",
                "Report any demand for money to your college placement cell and InternTrack administrators."
        ));
        return scan;
    }

    public void reportToAdmin(Long id, Long userId) {
        int rows = scamDetectorDao.markReported(id, userId);
        if (rows == 0) {
            throw new ResourceNotFoundException("Scam scan not found or unauthorized");
        }
    }
}
