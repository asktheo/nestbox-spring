package dk.theori.nestbox.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImportUtilTest {
    static final String GsheetText = "Tidsstempel\tKasse nummer\tStatus\tDato\tBemærkninger\tMailadresse\tUnger\tÆg\tArt\tTilstand\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "\t\t\t\t\t\t\t\t\t\n" +
            "29/03/2024 10.21.42\t1\tTom\t29/03/2024\t\tsigneagermose@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.22.01\t2\tTom\t29/03/2024\t\tsigneagermose@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.22.29\t3\tTom\t29/03/2024\t\tsigneagermose@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.22.52\t4\tTom\t29/03/2024\t\tsigneagermose@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.23.32\t5\tTom\t29/03/2024\t\tsigneagermose@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.24.07\t6\tTom\t29/03/2024\t\tsigneagermose@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.24.24\t7\tTom\t29/03/2024\t\tsigneagermose@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.24.42\t8\tTom\t29/03/2024\t\tsigneagermose@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.28.19\t402\tTom\t29/03/2024\tNyt tag\tsigneagermose@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "29/03/2024 10.29.09\t9\tTom\t29/03/2024\t\tsigneagermose@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.31.29\t12\tTom\t29/03/2024\tSkruelåg\tsigneagermose@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "29/03/2024 10.32.14\t201\tTom\t29/03/2024\t\tsigneagermose@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.34.33\t13\tRede\t29/03/2024\tMetalplade til hul\tsigneagermose@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "29/03/2024 10.36.18\t14\tRede\t29/03/2024\tKastanjer\tsigneagermose@gmail.com\t\t\t\tBør udskiftes (skriv bemærkning)\n" +
            "29/03/2024 10.37.58\t15\tTom\t29/03/2024\tMedtaget kasse\tsigneagermose@gmail.com\t\t\t\tBør udskiftes (skriv bemærkning)\n" +
            "29/03/2024 10.38.40\t16\tTom\t29/03/2024\tMedtaget\tsigneagermose@gmail.com\t\t\t\tBør udskiftes (skriv bemærkning)\n" +
            "29/03/2024 10.40.26\t19\tTom\t29/03/2024\t\tsigneagermose@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.40.56\t24\tTom\t29/03/2024\t\tsigneagermose@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.41.47\t17\tTom\t29/03/2024\t\tsigneagermose@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.42.33\t18\tTom\t29/03/2024\t\tsigneagermose@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.45.22\t21\tTom\t29/03/2024\t\tsigneagermose@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.46.58\t27\tTom\t29/03/2024\tSkruelåg\tsigneagermose@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "29/03/2024 10.47.40\t31\tTom\t29/03/2024\t\tsigneagermose@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.49.13\t26\tRede\t29/03/2024\t\tsigneagermose@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.50.28\t25\tTom\t29/03/2024\tMetalhul\tsigneagermose@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "29/03/2024 10.55.50\t56\tTom\t29/03/2024\t\tsigneagermose@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.57.41\t28\tTom\t29/03/2024\t\tsigneagermose@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 10.58.51\t34\tTom\t29/03/2024\tNyt tag\tphilipelbek@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "29/03/2024 11.00.25\t38\tTom\t29/03/2024\t\tphilipelbek@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 11.01.32\t29\tRede\t29/03/2024\tØdelagt indgangshul\tphilipelbek@gmail.com\t\t\t\tBør udskiftes (skriv bemærkning)\n" +
            "29/03/2024 11.03.30\t30\tRede\t29/03/2024\t\tphilipelbek@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 11.05.01\t39\tRede\t29/03/2024\t\tphilipelbek@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 11.08.21\t40\tTom\t29/03/2024\tStort hul i siden\tphilipelbek@gmail.com\t\t\t\tBør udskiftes (skriv bemærkning)\n" +
            "29/03/2024 11.09.16\t54\tTom\t29/03/2024\t\tphilipelbek@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 11.10.25\t53\tTom\t29/03/2024\t\tphilipelbek@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 11.12.15\t52\tTom\t29/03/2024\tNyt låg\tphilipelbek@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "29/03/2024 11.13.20\t204\tRede\t29/03/2024\t\tphilipelbek@gmail.com\t\t\t\t\n" +
            "29/03/2024 11.14.32\t50\tTom\t29/03/2024\t\tphilipelbek@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 11.18.13\t51\tRede\t29/03/2024\tNyt tag\tphilipelbek@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "29/03/2024 11.19.31\t42\tTom\t29/03/2024\t\tphilipelbek@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 11.20.05\t198\tRede\t29/03/2024\tKan ikke tages ned\tphilipelbek@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "29/03/2024 11.22.16\t199\tKassen er væk\t29/03/2024\tKassen er væk\tphilipelbek@gmail.com\t\t\t\tBør udskiftes (skriv bemærkning)\n" +
            "29/03/2024 11.24.58\t197\tRede\t29/03/2024\t\tphilipelbek@gmail.com\t\t\t\t\n" +
            "29/03/2024 11.26.21\t196\tRede\t29/03/2024\tMudret bund.  Kan heller ikke tages ned.\tphilipelbek@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "29/03/2024 11.28.32\t202\tTom\t29/03/2024\t\tphilipelbek@gmail.com\t\t\t\t\n" +
            "29/03/2024 11.31.12\t58\tTom\t29/03/2024\tFaldet ned. Ligger på jorden\tphilipelbek@gmail.com\t\t\t\tBør udskiftes (skriv bemærkning)\n" +
            "29/03/2024 11.32.22\t43\tTom\t29/03/2024\tMed skruelåg\tphilipelbek@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "29/03/2024 11.35.09\t47\tRede\t29/03/2024\t\tphilipelbek@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 11.36.10\t81\tTom\t29/03/2024\tFor stor åbning\tphilipelbek@gmail.com\t\t\t\tBør udskiftes (skriv bemærkning)\n" +
            "29/03/2024 11.37.31\t80\tTom\t29/03/2024\tStort hul i siden\tphilipelbek@gmail.com\t\t\t\tBør udskiftes (skriv bemærkning)\n" +
            "29/03/2024 11.38.46\t82\tTom\t29/03/2024\tMed skruelåg\tphilipelbek@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "29/03/2024 11.41.59\t79\tTom\t29/03/2024\tNyt tag\tphilipelbek@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "29/03/2024 11.42.38\t78\tTom\t29/03/2024\t\tphilipelbek@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 11.43.53\t77\tTom\t29/03/2024\t\tphilipelbek@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 11.46.27\t74\tUde af drift\t29/03/2024\tFaldet ned\tphilipelbek@gmail.com\t\t\t\tBør udskiftes (skriv bemærkning)\n" +
            "29/03/2024 11.47.46\t73\tTom\t29/03/2024\t\tphilipelbek@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 11.48.56\t71\tTom\t29/03/2024\tMed skruelåg\tphilipelbek@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "29/03/2024 11.50.19\t203\tTom\t29/03/2024\t\tphilipelbek@gmail.com\t\t\t\t\n" +
            "29/03/2024 11.53.03\t65\tTom\t29/03/2024\t\tphilipelbek@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 11.56.21\t69\tTom\t29/03/2024\t\tphilipelbek@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 11.58.27\t68\tTom\t29/03/2024\t\tphilipelbek@gmail.com\t\t\t\t\n" +
            "29/03/2024 12.02.19\t62\tRede\t29/03/2024\t\tphilipelbek@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 12.03.33\t60\tTom\t29/03/2024\t\tphilipelbek@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 12.04.54\t107\tTom\t29/03/2024\tFrontplade knækket\tphilipelbek@gmail.com\t\t\t\tBør udskiftes (skriv bemærkning)\n" +
            "29/03/2024 12.06.45\t108\tTom\t29/03/2024\tFaldet ned\tphilipelbek@gmail.com\t\t\t\tBør udskiftes (skriv bemærkning)\n" +
            "29/03/2024 12.09.51\t59\tTom\t29/03/2024\t\tphilipelbek@gmail.com\t\t\t\tGod\n" +
            "29/03/2024 12.12.38\t86\tTom\t29/03/2024\tNyt tag\tphilipelbek@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 10.51.18\t90\tTom\t31/03/2024\tNy frontplade\tsigneagermose@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 10.53.24\t89\tTom\t31/03/2024\tLåg m skruer\tjohannisson95@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 10.55.07\t92\tTom\t31/03/2024\tLåg m skruer\tjohannisson95@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 10.56.48\t95\tRede\t31/03/2024\t\tjohannisson95@gmail.com\t\t\t\tGod\n" +
            "31/03/2024 10.58.17\t104\tTom\t31/03/2024\t\tjohannisson95@gmail.com\t\t\t\tGod\n" +
            "31/03/2024 10.59.55\t118\tTom\t31/03/2024\t\tjohannisson95@gmail.com\t\t\t\tGod\n" +
            "31/03/2024 11.00.22\t117\tTom\t31/03/2024\t\tjohannisson95@gmail.com\t\t\t\tGod\n" +
            "31/03/2024 11.01.08\t120\tTom\t31/03/2024\tLåg m skruer\tjohannisson95@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 11.02.55\t103\tTom\t31/03/2024\tLåg m skruer, mange eksrementer\tjohannisson95@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 11.03.43\t205\tTom\t31/03/2024\t\tjohannisson95@gmail.com\t\t\t\tGod\n" +
            "31/03/2024 11.05.02\t102\tTom\t31/03/2024\tLåg m skruer\tjohannisson95@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 11.05.26\t101\tTom\t31/03/2024\tLåg m skruer \tjohannisson95@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 11.06.05\t115\tTom\t31/03/2024\t\tjohannisson95@gmail.com\t\t\t\tGod\n" +
            "31/03/2024 11.07.17\t105\tRede\t31/03/2024\tLåg m skruer \tjohannisson95@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 11.07.50\t100\tTom\t31/03/2024\t\tjohannisson95@gmail.com\t\t\t\tGod\n" +
            "31/03/2024 11.08.06\t206\tTom\t31/03/2024\t\tjohannisson95@gmail.com\t\t\t\tGod\n" +
            "31/03/2024 11.09.43\t305\tMangler låg\t31/03/2024\tMangler låg\tjohannisson95@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 11.10.07\t306\tMangler låg \t31/03/2024\tMangler låg \tjohannisson95@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 11.13.04\t97\tRede\t31/03/2024\tLåg m skruer \tjohannisson95@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 11.13.57\t98\tTom\t31/03/2024\t\tjohannisson95@gmail.com\t\t\t\tGod\n" +
            "31/03/2024 11.15.15\t93\tTom\t31/03/2024\tFrontplade løs, ophæng ødelagt\tjohannisson95@gmail.com\t\t\t\tBør udskiftes (skriv bemærkning)\n" +
            "31/03/2024 11.16.02\t88\tTom\t31/03/2024\tLåg m skruer \tjohannisson95@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 11.24.14\t126\tTom\t31/03/2024\tLåg m skruer \tjohannisson95@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 11.25.23\t127\tTom\t31/03/2024\tLåg m skruer \tjohannisson95@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 11.26.28\t131\tTom\t31/03/2024\tLåg m skruer \tjohannisson95@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 11.27.15\t128\tTom\t31/03/2024\tLåg m skruer \tjohannisson95@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 11.28.20\t132\tTom\t31/03/2024\tMangler tag\tjohannisson95@gmail.com\t\t\t\tBør udskiftes (skriv bemærkning)\n" +
            "31/03/2024 11.30.58\t502\tMangler front\t31/03/2024\tMangler front\tjohannisson95@gmail.com\t\t\t\tBør udskiftes (skriv bemærkning)\n" +
            "31/03/2024 11.33.03\t401\tTom\t31/03/2024\tLåg m skruer \tjohannisson95@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 11.33.15\t207\tTom\t31/03/2024\t\tjohannisson95@gmail.com\t\t\t\tGod\n" +
            "31/03/2024 11.35.41\t139\tTom\t31/03/2024\tMangler låg\tjohannisson95@gmail.com\t\t\t\tBør udskiftes (skriv bemærkning)\n" +
            "31/03/2024 11.39.18\t145\tLigger på jorden\t31/03/2024\tSkal hænges op igen, ligger på jorden\tjohannisson95@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 11.41.26\t134\tTom\t31/03/2024\tOphæng og tag udskiftes\tjohannisson95@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 11.42.18\t135\tMangler låg\t31/03/2024\tMangler låg\tjohannisson95@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 11.43.13\t136\tTom\t31/03/2024\tIkke stige, låg m skruer\tjohannisson95@gmail.com\t\t\t\tGod\n" +
            "31/03/2024 11.47.00\t208\tTom\t31/03/2024\t\tjohannisson95@gmail.com\t\t\t\tGod\n" +
            "31/03/2024 11.47.53\t156\tTom\t31/03/2024\t\tjohannisson95@gmail.com\t\t\t\tGod\n" +
            "31/03/2024 11.49.09\t148\tTom\t31/03/2024\t\tjohannisson95@gmail.com\t\t\t\tBør udskiftes (skriv bemærkning)\n" +
            "31/03/2024 11.51.05\t137\tTom\t31/03/2024\t\tjohannisson95@gmail.com\t\t\t\tGod\n" +
            "31/03/2024 11.52.23\t140\tTom\t31/03/2024\tMangler låg\tjohannisson95@gmail.com\t\t\t\tBør udskiftes (skriv bemærkning)\n" +
            "31/03/2024 11.53.01\t138\tTom\t31/03/2024\tLåg m skruer \tjohannisson95@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 11.55.23\t155\tTom\t31/03/2024\t\tjohannisson95@gmail.com\t\t\t\tGod\n" +
            "31/03/2024 11.58.48\t142\tTom\t31/03/2024\tLåg m skruer \tjohannisson95@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 12.01.44\t153\tTom\t31/03/2024\t\tjohannisson95@gmail.com\t\t\t\tGod\n" +
            "31/03/2024 12.02.52\t154\tTom\t31/03/2024\tLåg m skruer \tjohannisson95@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)\n" +
            "31/03/2024 12.33.58\t144\tTom\t31/03/2024\tLåg m skruer, sidder ikke godt på\tjohannisson95@gmail.com\t\t\t\tReparation anbefales (skriv bemærkning)";

    @Test
    public void testUtilFunction(){
        String jsonObject = ImportUtil.TSV2Json(GsheetText);
        System.out.println(jsonObject);
    }

    @Test
    public void testLineLength2(){
        String[] splits = "x\ty\tz\t\ta".split("\t");
        for (String a : splits){
            System.out.println(a);
        }
    }

}